//
// Created by rui on 2018/10/26.
//
#endif

#include<stdio.h>
#include<sys/types.h>
#include<sys/socket.h>
#include<netinet/in.h>
#include<arpa/inet.h>
#include<assert.h>
#include<string.h>
#include<errno.h>
#include<netdb.h>
#include<assert.h>
#include<sys/types.h>
#include<fcntl.h>
#include<unistd.h>
#include<stdlib.h>
#define HP_MAX_LEN 40960

int fd;

/* RTSP 请求数据包 */
struct rtsp_pack{
    unsigned char buff[HP_MAX_LEN];
    int pos;
};

/* 丢包函数 */
void drop_packg(int sfd, int length) {
    int ret = 0;
    char buff[40960];
    int lengthh = 0;
    ret = recv(sfd, buff, length, 0);
    if (ret == length) {
        return;
    }
    lengthh = length - ret;
    while(lengthh > 0) {
        printf("adfasdfasdfadf\n");
        ret = recv(sfd, buff, lengthh, 0);
        lengthh = lengthh - ret;
    }
    return ;
}

typedef struct rtp_header {
#ifdef ORTP_BIGENDIAN
    uint16_t version:2;
    uint16_t padbit:1;
    uint16_t extbit:1;
    uint16_t cc:4;
    uint16_t markbit:1;
    uint16_t paytype:7;
#else
    uint16_t cc:4;
    uint16_t extbit:1;
    uint16_t padbit:1;
    uint16_t version:2;
    uint16_t paytype:7;
    uint16_t markbit:1;
#endif
    uint16_t seq_number;
    uint32_t timestamp;
    uint32_t ssrc;
    uint32_t csrc[16];
} rtp_header_t;

/* TCP 数据包头 */
typedef struct {
    unsigned char magic;
    unsigned char channel;
    unsigned short length;
}ST_Magic;

/* RTP Header */
typedef struct {
    /* byte 0 */
    unsigned char csrc_len:4;   /* CC expect 0 */
    unsigned char extension:1;  /* X  expect 1, see RTP_OP below */
    unsigned char padding:1;    /* P  expect 0 */
    unsigned char version:2;    /* V  expect 2 */
    /* byte 1 */
    unsigned char payload:7;    /* PT  RTP_PAYLOAD_RTSP */
    unsigned char marker:1;     /* M   expect 1 */
    /* byte 2,3 */
    unsigned short seq_no;      /* sequence number */
    /* byte 4-7 */
    unsigned long timestamp;    /* timestamp */
    /* byte 8-11 */
    unsigned long ssrc;         /* stream number is used here. */
} RTPHeader;/* 12 bytes */

/* NALU Header */
typedef struct{
    unsigned char Type:5;
    unsigned char NRI:2;
    unsigned char F:1;
}NaluHeader;

/* FU_indicator + FU_Header */
typedef struct{
    unsigned char Type:5;
    unsigned char NRI:2;
    unsigned char F:1;
    unsigned char fuType:5;
    unsigned char R:1;
    unsigned char E:1;
    unsigned char S:1;
}FuNaluHeader;

/**  **/
int rtsp_addline( struct rtsp_pack *hp,char *newline ) {
    int current_pos = hp->pos;
    int newline_len = strlen(newline);
    if( current_pos + newline_len + 2 >= HP_MAX_LEN )
        return -1;
    if( (current_pos <= 4) && (current_pos != 0) )
        return -1;
    if( hp->pos == 0 ){
        memcpy( hp->buff,newline,newline_len );
    }else{
        current_pos -= 2;
        memcpy( hp->buff + current_pos,newline,newline_len );
    }
    current_pos += newline_len;
    memcpy( hp->buff + current_pos,"\r\n\r\n",4 );
    current_pos += 4;
    hp->pos = current_pos;
    return 0;
}

int next[32];
void match_pre( char *pattern,int *next,int pattern_len ) {
    int matched = 0;
    int begin = 1;
    next[0] = 0;
    while( begin + matched < pattern_len ) {
        if( pattern[begin + matched ] == pattern[matched] ) {
            matched++;
            next[begin + matched - 1] = matched;
        }else{
            if( matched == 0 ){
                begin++;
            }else{
                begin += matched - next[ matched - 1];
                matched = next[ matched - 1];
            }
        }
    }
    return;
}

int match( char *str,char *pattern,int *len )
{
    int pattern_len = strlen( pattern );
    int str_len = strlen(str);
    int matched = 0;
    int begin = 0;
    char *p = NULL;
    if( pattern_len > 32 )
        return -1;
    match_pre( pattern,next,pattern_len );
    while( begin + matched < str_len ){
        if( pattern[matched] == str[begin + matched] ){
            matched++;
            if( matched == pattern_len ){
                p = str + begin + matched;
                while( *p != '\r' || *(p + 1) != '\n')
                    p++;
                *len = p - (str + begin);
                return begin;
            }
        }else{
            if( matched == 0 ){
                begin++;
            }else{
                begin += matched - next[ matched - 1];
                matched = next[ matched - 1];
            }
        }
    }
    return -1;
}

char *line_match( char *str,char *pattern ){
    int len;
    int index = match( str,pattern,&len );
    if( index == -1 )
        return NULL;
    char *buff = (char *)malloc( sizeof(char) * len + 1 );
    memcpy( buff,&str[index],len );
    buff[len] = '\0';
    return buff;
}

int main(){
    struct rtsp_pack rtsp;                                          //rtsp的数据包
    struct sockaddr_in sa;
    int sfd = socket(AF_INET, SOCK_STREAM, 0);                      //socket句柄
    int ret = -1;
    unsigned char buff[40960];
    rtsp.pos = 0;
    int size = 0;
    fd = open("./xxx.mpeg", O_WRONLY|O_CREAT);
    if (-1 == fd) {
        printf("open file faild\n");
        return -1;
    }

    memset(&rtsp, 0, sizeof(struct rtsp_pack));
    sa.sin_family = AF_INET;
    sa.sin_port = htons(554);
    sa.sin_addr.s_addr = inet_addr("184.72.239.149");
    ret = connect(sfd, (struct sockaddr *)&sa, sizeof(sa));
    if( ret < 0 ){
        printf("connect error,%d\n",errno);
        return -1;
    }
    printf("connect success!!!\n");

    /**/
    rtsp_addline(&rtsp, (char *)"OPTIONS rtsp://184.72.239.149:554/vod/mp4://BigBuckBunny_175k.mov RTSP/1.0");
    rtsp_addline(&rtsp, (char *)"CSeq: 1");
    rtsp_addline(&rtsp, (char *)"User-Agent: LibVLC/3.0.2 (LIVE555 Streaming Media v2016.11.28)");
    rtsp_addline(&rtsp, (char *)"\r\n");

    memset(buff, 0, sizeof(buff));
    ret = send(sfd, rtsp.buff, rtsp.pos, 0);
    ret = recv(sfd, buff, 8192, 0);
    printf("ret = %d\n", ret);
    printf("%s\n", buff);

    memset(&rtsp, 0, sizeof(struct rtsp_pack));
    memset(buff, 0, sizeof(buff));
    rtsp_addline(&rtsp, (char *)"DESCRIBE rtsp://184.72.239.149:554/vod/mp4://BigBuckBunny_175k.mov RTSP/1.0");
    rtsp_addline(&rtsp, (char *)"CSeq: 2");
    rtsp_addline(&rtsp, (char *)"User-Agent: LibVLC/3.0.2 (LIVE555 Streaming Media v2016.11.28)");
    rtsp_addline(&rtsp, (char *)"Accept: application/sdp");
    rtsp_addline(&rtsp, (char *)"\r\n");

    ret = send(sfd, rtsp.buff, rtsp.pos, 0);
    ret = recv(sfd, buff, 8192, 0);
    printf("ret = %d\n", ret);

    printf("%s\n", buff);

    char *p = line_match(buff, "Session:");
    char Session[128];
    memset(Session, 0, sizeof(Session));

    for( int i = 0; ; i++ ){
        if( p[i] == ';'){
            Session[i] = '\0';
            break;
        } Session[i] = p[i];
    }
    printf("session = %s\n", Session);
    memset(&rtsp, 0, sizeof(struct rtsp_pack));
    memset(buff, 0, sizeof(buff));

    rtsp_addline(&rtsp, (char *)"SETUP rtsp://184.72.239.149:554/vod/mp4://BigBuckBunny_175k.mov/trackID=2 RTSP/1.0");
    rtsp_addline(&rtsp, (char *)"CSeq: 3");
    rtsp_addline(&rtsp, (char *)"User-Agent: LibVLC/3.0.2 (LIVE555 Streaming Media v2016.11.28)");
    rtsp_addline(&rtsp, (char *)"Transport: RTP/AVP/TCP;unicast;");

    ret = send(sfd, rtsp.buff, rtsp.pos, 0);
    ret = recv(sfd, buff, 8192, 0);
    printf("ret = %d\n", ret);

    printf("%s\n", buff);

#if 0
    memset(&rtsp, 0, sizeof(struct rtsp_pack));
    memset(buff, 0, sizeof(buff));

    rtsp_addline(&rtsp, (char *)"SETUP rtsp://184.72.239.149:554/vod/mp4://BigBuckBunny_175k.mov/trackID=2 RTSP/1.0");
    rtsp_addline(&rtsp, (char *)"CSeq: 4");
    rtsp_addline(&rtsp, (char *)"User-Agent: LibVLC/3.0.2 (LIVE555 Streaming Media v2016.11.28)");
    rtsp_addline(&rtsp, (char *)"Transport: RTP/AVP/TCP;unicast;");
    rtsp_addline(&rtsp, Session); rtsp_addline(&rtsp, "\n\r");

    ret = send(sfd, rtsp.buff, rtsp.pos, 0);
    ret = recv(sfd, buff, 8192, 0);
    printf("ret = %d\n", ret);

    printf("%s\n", buff);
#endif
    memset(&rtsp, 0, sizeof(struct rtsp_pack));
    memset(buff, 0, sizeof(buff));
    rtsp_addline(&rtsp, (char *)"PLAY rtsp://184.72.239.149:554/vod/mp4://BigBuckBunny_175k.mov/ RTSP/1.0");
    rtsp_addline(&rtsp, (char *)"CSeq: 5");
    rtsp_addline(&rtsp, (char *)"User-Agent: LibVLC/3.0.2 (LIVE555 Streaming Media v2016.11.28)");
    rtsp_addline(&rtsp, Session);
    rtsp_addline(&rtsp, (char *)"Range: npt=0.000-");
    rtsp_addline(&rtsp, "\n\r");

    ret = send(sfd, rtsp.buff, rtsp.pos, 0);
    ret = recv(sfd, buff, 8192, 0);
    printf("ret = %d\n", ret);

    printf("%s\n", buff);

    RTPHeader *rtpHeader = NULL;
    ST_Magic *stMagic = NULL;
    char header[5];
    NaluHeader *naluHeader = NULL;
    memset(header, 0, sizeof(header));
    int lengthh = 0;
    char H264_Header[]={0x00, 0x00, 0x00, 0x01};
    /* sps及pps 这里直接使用手动解析出来的 */
    char sps[]={0x67 ,0x42 ,0xc0 ,0x1e ,0xd9 ,0x03 ,0xc5 ,0x68 ,0x40 ,0x00 ,0x00 ,0x03 ,0x00 ,0x40 ,0x00 ,0x00 ,0x0c ,0x03 ,0xc5 ,0x8b ,0x92};
    char pps[]={0x68 ,0xcb ,0x8c ,0xb2};
    int Type;
    int NRI;
    int F;
    char I_SLIC[]={0X65};
    FuNaluHeader * fuNaluHeader = NULL;
    write(fd, H264_Header, sizeof(H264_Header));
    write(fd, sps, sizeof(sps));
    write(fd, H264_Header, sizeof(H264_Header));
    write(fd, pps, sizeof(pps));

    while(1) {
        /*接收4字节，判断这个包的长度，然后再接收相应长度的数据，解决粘包问题*/
        ret = recv(sfd, header, 4, 0);
        stMagic = (ST_Magic *) header;
        /*判断魔数，且是RTP包才接收*/
        if (stMagic->magic == 0x24 && !(stMagic->channel % 2)) {
            printf("magic = %d\n", stMagic->magic);
            printf("channel = %d\n", stMagic->channel);
            printf("length = %d\n", ntohs(stMagic->length));
            lengthh = ntohs(stMagic->length);
            printf("lengthh = %d\n", lengthh);
            /*接收第一包数据*/
            ret = recv(sfd, buff, lengthh, 0);
            rtpHeader = (RTPHeader *) buff;
            printf("RTP packg\n");
            printf("version = %d\n", rtpHeader->version);
            printf("padding = %d\n", rtpHeader->padding);
            printf("extension = %d\n", rtpHeader->extension);
            printf("csrc_len =  %d\n", rtpHeader->csrc_len);
            printf("marker = %d\n", rtpHeader->marker);
            printf("payload = %d\n", rtpHeader->payload);
            printf("seq_no = %d\n", ntohs(rtpHeader->seq_no));
            lengthh = lengthh - ret;
            naluHeader = (NaluHeader *) (buff + 12);
            printf("type = %d\n", naluHeader->Type);
            printf("NRI = %d\n", naluHeader->NRI);
            printf("F = %d\n", naluHeader->F);
            /*判断nalu类型*/
            if ((naluHeader->Type > 0) && (naluHeader->Type < 25))//1~23代表单包，如果是单包，直接在前面增加H264_Header（0001）然后将数据写入到文件中
            {
                printf("type =================================== naluHeader->Type = %d\n", naluHeader->Type);
                printf("danbao=====================================================\n");
                write(fd, H264_Header, sizeof(H264_Header));
                write(fd, buff + 12, ret - 12);
            } else if (naluHeader->Type == 28) //28代表分片，如果是分片则判断是不是一帧的开始，如果是，则增加H264_Header然后将数据写入到文件中，如果不是，则直接将数据写入到文件中
            {
                printf("fenbao===============================================================\n");
                fuNaluHeader = (FuNaluHeader *) (buff + 12);
                printf("fenbao leixing =============================================================== fuType = %d\n",
                       fuNaluHeader->fuType);
                if (fuNaluHeader->S == 1) //first slice
                {
                    printf("fuType = %d\n", fuNaluHeader->fuType); //start
                    write(fd, H264_Header, sizeof(H264_Header));
                    write(fd, I_SLIC, 1);
                }
                write(fd, buff + 14, ret - 14);
            } else {
                printf("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!type = %d\n", naluHeader->Type);
            }
            while (lengthh > 0) //while 循环则是接收一次没有接收完的包数据
            {
                printf("jinru while===============\n");
                ret = recv(sfd, buff, lengthh, 0);
                write(fd, buff, ret);
                lengthh -= ret;
            }
            printf("\n");
            memset(buff, 0, sizeof(buff));
        }else {
            printf("length ===================================================================================############## %d\n",
                   ntohs(stMagic->length));
            /*丢掉不是RTP的包*/
            drop_packg(sfd, ntohs(stMagic->length));
        }
    }
    close(fd);
    close(sfd);
    return 0;
}

