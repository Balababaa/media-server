package com.xbyrh;

import org.bytedeco.javacpp.avcodec;
import org.bytedeco.javacv.*;

/**
 * create at 2021/4/20
 *
 * @author chenxinhui
 */
public class GrabStream {

    /**
     * 按帧录制视频
     *
     * @param inputFile-该地址可以是网络直播/录播地址，也可以是远程/本地文件路径
     * @param outputFile
     *            -该地址只能是文件地址，如果使用该方法推送流媒体服务器会报错，原因是没有设置编码格式
     * @throws FrameGrabber.Exception
     * @throws FrameRecorder.Exception
     * @throws org.bytedeco.javacv.FrameRecorder.Exception
     */
    public static void frameRecord(String inputFile, String outputFile, int audioChannel)
            throws Exception, org.bytedeco.javacv.FrameRecorder.Exception {

        boolean isStart=true;//该变量建议设置为全局控制变量，用于控制录制结束
        // 获取视频源
        FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(inputFile);
        // 流媒体输出地址，分辨率（长，高），是否录制音频（0:不录制/1:录制）
        FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(outputFile, 1280, 720, audioChannel);
        recorder.setVideoCodec(avcodec.AV_CODEC_ID_H264); // avcodec.AV_CODEC_ID_H264，编码
        recorder.setFrameRate(25D);
        // 开始取视频源
        recordByFrame(grabber, recorder, isStart);
    }

    private static void recordByFrame(FFmpegFrameGrabber grabber, FFmpegFrameRecorder recorder, Boolean status)
            throws Exception, org.bytedeco.javacv.FrameRecorder.Exception {
        try {//建议在线程中使用该方法
            grabber.start();
            recorder.start();
            Frame frame = null;

            long startTime = System.currentTimeMillis();

            while (status&& (frame = grabber.grabFrame()) != null) {
                recorder.record(frame);
                if (System.currentTimeMillis() - startTime >= 10000) {
                    break;
                }
            }
            recorder.stop();
            grabber.stop();
        } finally {
            if (grabber != null) {
                grabber.stop();
            }
        }
    }

    public static void main(String[] args)
            throws Exception {

        String inputFile = "https://flvopen.ys7.com:9188/openlive/6e0b2be040a943489ef0b9bb344b96b8.hd.flv";
        // Decodes-encodes
        String outputFile = "record.mp4";
        frameRecord(inputFile, outputFile,1);
    }
}
