package com.jiuxiao.base.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Mp4VideoUtil extends VideoUtil {

    String ffmpegPath;
    String videoPath;
    String mp4Name;
    String mp4FolderPath;

    public Mp4VideoUtil(String ffmpegPath, String videoPath, String mp4Name, String mp4FolderPath){
        super(ffmpegPath);
        this.ffmpegPath = ffmpegPath;
        this.videoPath = videoPath;
        this.mp4Name = mp4Name;
        this.mp4FolderPath = mp4FolderPath;
    }
    //清除已生成的mp4
    private void clear_mp4(String mp4_path){
        //删除原来已经生成的m3u8及ts文件
        File mp4File = new File(mp4_path);
        if(mp4File.exists() && mp4File.isFile()){
            mp4File.delete();
        }
    }
    /**
     * 视频编码，生成mp4文件
     * @return 成功返回success，失败返回控制台日志
     */
    public String generateMp4(){
        //清除已生成的mp4
        clear_mp4(mp4FolderPath);
        /*
        ffmpeg.exe -i  lucene.avi -c:v libx264 -s 1280x720 -pix_fmt yuv420p -b:a 63k -b:v 753k -r 18 .\lucene.mp4
         */
        List<String> commend = new ArrayList<String>();
        commend.add(ffmpegPath);
        commend.add("-i");
        commend.add(videoPath);
        commend.add("-c:v");
        commend.add("libx264");
        commend.add("-y");//覆盖输出文件
        commend.add("-s");
        commend.add("1280x720");
        commend.add("-pix_fmt");
        commend.add("yuv420p");
        commend.add("-b:a");
        commend.add("63k");
        commend.add("-b:v");
        commend.add("753k");
        commend.add("-r");
        commend.add("18");
        commend.add(mp4FolderPath);
        String outstring = null;
        try {
            ProcessBuilder builder = new ProcessBuilder();
            builder.command(commend);
            //将标准输入流和错误输入流合并，通过标准输入流程读取信息
            builder.redirectErrorStream(true);
            Process p = builder.start();
            outstring = waitFor(p);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        Boolean check_video_time = this.check_video_time(videoPath, mp4FolderPath);
        if(!check_video_time){
            return outstring;
        }else{
            return "success";
        }
    }
}