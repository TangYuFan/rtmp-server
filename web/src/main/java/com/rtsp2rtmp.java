package com;


import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacpp.avcodec;
import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_objdetect;
import org.bytedeco.javacv.*;

/**
 * @desc : rtsp推流到rtmp服务器
 * @auth : TYF
 * @date : 2019-04-30 - 15:39
 */
public class rtsp2rtmp {


    public static void recordPush(String inputFile, String outputFile, int v_rs) throws Exception {
        Loader.load(opencv_objdetect.class);
        long startTime=0;
        FrameGrabber grabber =FFmpegFrameGrabber.createDefault(inputFile);
        grabber.setOption("rtsp_transport", "tcp"); //默认udp丢包严重图像卡顿跳帧
        try {
            grabber.start();
        } catch (Exception e) {
            try {
                grabber.restart();
            } catch (Exception e1) {
                throw e;
            }
        }
        OpenCVFrameConverter.ToIplImage converter = new OpenCVFrameConverter.ToIplImage();
        Frame grabframe =grabber.grab();
        opencv_core.IplImage grabbedImage =null;
        if(grabframe!=null){
            System.out.println("get 1 frame success !");
            grabbedImage = converter.convert(grabframe);
        }else{
            System.out.println("get 1 frame fail !");
        }
        FrameRecorder recorder;
        try {
            recorder = FrameRecorder.createDefault(outputFile, 1280, 720);
        } catch (FrameRecorder.Exception e) {
            throw e;
        }
        recorder.setVideoCodec(avcodec.AV_CODEC_ID_H264);
        recorder.setFormat("flv");
        recorder.setFrameRate(v_rs);
        recorder.setGopSize(v_rs);
        System.out.println("rtsp push to rtmp ready !");
        try {
            recorder.start();
        } catch (FrameRecorder.Exception e) {
            try {
                System.out.println("recorder start fail ！");
                if(recorder!=null)
                {
                    System.out.println("recorder stop ！");
                    recorder.stop();
                    System.out.println("recorder start ！");
                    recorder.start();
                }
            } catch (FrameRecorder.Exception e1) {
                throw e;
            }
        }
        System.out.println("rtsp push to rtmp start !");
        while ((grabframe=grabber.grab()) != null) {
            System.out.println("push.. ");
            grabbedImage = converter.convert(grabframe);
            Frame rotatedFrame = converter.convert(grabbedImage);
            if (startTime == 0) {
                startTime = System.currentTimeMillis();
            }
            recorder.setTimestamp(1000 * (System.currentTimeMillis() - startTime));//时间戳
            if(rotatedFrame!=null){
                recorder.record(rotatedFrame);
            }
        }
        recorder.stop();
        recorder.release();
        grabber.stop();
        System.exit(2);
    }

    //java -jar rtsp2rtmp-0.0.1-SNAPSHOT.jar rtsp://192.168.1.125:556/0 rtmp://192.168.1.201/live/pushFlow
    public static void main(String[] args) throws Exception {
        String in = "rtsp://184.72.239.149/vod/mp4://BigBuckBunny_175k.mov";
        String out = "rtmp://192.168.1.201/live/pushFlow";
        System.out.println("in:"+in);
        System.out.println("out:"+out);
        recordPush(in, out, 100);
    }

}
