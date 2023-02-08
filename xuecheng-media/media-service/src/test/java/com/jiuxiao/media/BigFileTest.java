package com.jiuxiao.media;

import org.apache.commons.codec.digest.DigestUtils;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * 大文件上传测试
 * @Author: 悟道九霄
 * @Date: 2023年02月07日 15:27
 * @Version: 1.0.0
 */
public class BigFileTest {

    final String sourceFilePath = "E:\\安四点.mkv";   //源文件
    final String chunkFilePath = "E:\\chunk\\"; //分块文件存储路径
    final String mergeFilePath = "E:\\安四点_001.mp4";

    @Test
    public void testFileChunk() throws IOException {
        File sourceFile = new File(sourceFilePath);
        File chunkFile = new File(chunkFilePath);
        if (!chunkFile.exists()) {
            chunkFile.mkdirs();
        }
        int chunkSize = 1024 * 1024 * 1; //分块大小
        long chunkNums = (long) Math.ceil(sourceFile.length() * 1.0 / chunkSize); //分块数量

        //使用流对象读取源文件，然后向分块文件写数据，达到分块大小后不再写入
        RandomAccessFile accessFileRead = new RandomAccessFile(sourceFile, "r");
        byte[] bytes = new byte[1024];
        for (long i = 0; i < chunkNums; i++) {
            File file = new File(chunkFilePath + i);
            if (file.exists()) { //若分块文件存在则删除
                file.delete();
            }
            //向分块文件写数据流对象
            boolean newFile = file.createNewFile();
            if (newFile) {
                int len = -1;
                RandomAccessFile accessFileWrite = new RandomAccessFile(file, "rw");
                while ((len = accessFileRead.read(bytes)) != -1) {
                    //项文件中写数据
                    accessFileWrite.write(bytes, 0, len);
                    //达到分片大小后不再写
                    if (file.length() >= chunkSize) {
                        break;
                    }
                }
                accessFileWrite.close();
            }
        }
        accessFileRead.close();
    }

    @Test
    public void testFileMerge() throws IOException {
        File sourceFile = new File(sourceFilePath);
        File chunkFile = new File(chunkFilePath);
        if (!chunkFile.exists()) {
            chunkFile.mkdirs();
        }
        File mergeFile = new File(mergeFilePath);
        boolean newFile = mergeFile.createNewFile();

        //依次按照顺序读取分块文件，向合并文件写数据
        File[] chunkFiles = chunkFile.listFiles();
        assert chunkFiles != null;
        //按照文件名升序排序
        List<File> fileList = Arrays.asList(chunkFiles);
        fileList.sort(new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                return Integer.parseInt(o1.getName()) - Integer.parseInt(o2.getName());
            }
        });

        //依次读取分块文件并写入到合并文件 accessFileWrite
        RandomAccessFile accessFileWrite = new RandomAccessFile(mergeFile, "rw");
        byte[] bytes = new byte[1024];
        for (File file : fileList) {
            int len = -1;
            RandomAccessFile r = new RandomAccessFile(file, "r");//读取分块文件的流对象
            while ((len = r.read(bytes)) != -1){
                accessFileWrite.write(bytes, 0, len);
            }
        }

        //校验合并后的文件是否正确:不能比大小，可以考虑使用 md5 比较
        FileInputStream sourceFileStream = new FileInputStream(sourceFile);
        FileInputStream mergeFileStream = new FileInputStream(mergeFile);
        String sourceMd5 = DigestUtils.md5Hex(sourceFileStream);
        String mergeMd5 = DigestUtils.md5Hex(mergeFileStream);
        if (sourceMd5.equals(mergeMd5)){
            System.out.println("合并成功");
        }
    }
}