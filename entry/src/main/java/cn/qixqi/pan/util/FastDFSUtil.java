package cn.qixqi.pan.util;

import cn.qixqi.pan.model.FastDFSFile;
import org.csource.common.MyException;
import org.csource.common.NameValuePair;
import org.csource.fastdfs.*;

import java.io.IOException;
import java.net.InetAddress;


/**
 *  FastDFS 文件管理
 *      文件上传
 *      文件删除
 *      文件下载
 *      文件信息获取
 *      storage 信息获取
 *      tracker 信息获取
 */
public class FastDFSUtil {
    /**
     * 初始化连接
     */
    static {
        try {
            // String path = System.getProperty("user.dir") + "\\target\\classes\\dfs_client.conf";
            String path = "assets/entry/resources/rawfile/fastdfs/dfs_client.conf";
            // 初始化连接 tracker 链接信息
            ClientGlobal.init(path);
        } catch(IOException e){
            e.printStackTrace();
        } catch (MyException e){
            e.printStackTrace();
        }
    }


    /**
     * 文件上传
     * @param fastDFSFile
     * @return
     * @throws IOException
     * @throws MyException
     */
    public static String upload(FastDFSFile fastDFSFile) throws IOException, MyException{
        // 新建 TrackerClient 对象，访问 TrackerServer
        TrackerClient trackerClient = new TrackerClient();
        // 获取连接，获取 TrackerServer 对象
        TrackerServer trackerServer = trackerClient.getTrackerServer();
        // 获取 StorageServer 的连接信息
        StorageServer storageServer = trackerClient.getStoreStorage(trackerServer);
        // 新建 StorageClient 对象
        StorageClient storageClient = new StorageClient(trackerServer, storageServer);

        // 虚拟附加参数
        NameValuePair[] list = new NameValuePair[1];
        NameValuePair nameValuePair = new NameValuePair("address", "大连");
        list[0] = nameValuePair;

        // 访问 storage服务器，实现文件上传
        // 返回文件上传后的存储信息
        // param1: 上传文件名
        // param2: 文件扩展名
        // param3: 附加信息 NameValuePair[]
        String[] rsp = storageClient.upload_file(fastDFSFile.getName(), fastDFSFile.getExt(), list);

        // 文件服务器
        // rsp[0] 分组信息
        // rsp[1] 分组内的具体存储位置
        String ipPort = "http://ali.qixqi.cn:8888/";
        String url = rsp[0] + "/" + rsp[1];

        return ipPort + url;
    }


    /**
     * 获取文件信息
     * @param groupName
     * @param remoteFileName
     * @return
     * @throws IOException
     * @throws MyException
     */
    public static FileInfo getFileInfo(String groupName, String remoteFileName) throws IOException, MyException{
        TrackerClient trackerClient = new TrackerClient();
        TrackerServer trackerServer = trackerClient.getTrackerServer();
        StorageServer storageServer = trackerClient.getStoreStorage(trackerServer);
        StorageClient storageClient = new StorageClient(trackerServer, storageServer);

        FileInfo fileInfo = storageClient.get_file_info(groupName, remoteFileName);
        return fileInfo;
    }


    /**
     * 文件下载
     * @param groupName
     * @param remoteFileName
     * @return
     * @throws IOException
     * @throws MyException
     */
    public static byte[] download(String groupName, String remoteFileName) throws IOException, MyException{
        TrackerClient trackerClient = new TrackerClient();
        TrackerServer trackerServer = trackerClient.getTrackerServer();
        StorageServer storageServer = trackerClient.getStoreStorage(trackerServer);
        StorageClient storageClient = new StorageClient(trackerServer, storageServer);

        byte[] bytes = storageClient.download_file(groupName, remoteFileName);
        return bytes;
    }


    /**
     * 文件删除
     * @param groupName
     * @param remoteFileName
     * @return
     * @throws IOException
     * @throws MyException
     */
    public static int delete(String groupName, String remoteFileName) throws IOException, MyException{
        TrackerClient trackerClient = new TrackerClient();
        TrackerServer trackerServer = trackerClient.getTrackerServer();
        StorageServer storageServer = trackerClient.getStoreStorage(trackerServer);
        StorageClient storageClient = new StorageClient(trackerServer, storageServer);

        int result = storageClient.delete_file(groupName, remoteFileName);
        return result;
    }


    /**
     * 获取 storage 信息
     * @return
     * @throws IOException
     * @throws MyException
     */
    public static String getStorage() throws IOException, MyException{
        TrackerClient trackerClient = new TrackerClient();
        TrackerServer trackerServer = trackerClient.getTrackerServer();
        StorageServer storageServer = trackerClient.getStoreStorage(trackerServer);

        // 获取 storage
        int storePathIndex = storageServer.getStorePathIndex();
        InetAddress address = storageServer.getInetSocketAddress().getAddress();
        String hostName = storageServer.getInetSocketAddress().getHostName();
        String hostString = storageServer.getInetSocketAddress().getHostString();
        int port = storageServer.getInetSocketAddress().getPort();
        StringBuilder sb = new StringBuilder();
        sb.append("storePathIndex is ").append(storePathIndex).append("; ")
                .append("address is ").append(address).append("; ")
                .append("hostName is ").append(hostName).append("; ")
                .append("hostString is ").append(hostString).append("; ")
                .append("port is ").append(port).append("; ");
        return sb.toString();
    }


    /**
     * 获取 storage 组的 ip 和 端口
     * @param groupName
     * @param remoteFileName
     * @return
     * @throws IOException
     * @throws MyException
     */
    public static ServerInfo[] getStorageGroupInfo(String groupName, String remoteFileName) throws IOException, MyException{
        TrackerClient trackerClient = new TrackerClient();
        TrackerServer trackerServer = trackerClient.getTrackerServer();

        ServerInfo[] fetchStorage = trackerClient.getFetchStorages(trackerServer, groupName, remoteFileName);
        return fetchStorage;
    }


    /**
     * 获取 tracker 地址
     * @return
     * @throws IOException
     */
    public static String getTrackerInfo() throws IOException{
        TrackerClient trackerClient = new TrackerClient();
        TrackerServer trackerServer = trackerClient.getTrackerServer();

        String hostString = trackerServer.getInetSocketAddress().getHostString();
        return hostString;
    }
}
