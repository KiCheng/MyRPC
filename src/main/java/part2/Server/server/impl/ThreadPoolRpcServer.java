package part2.Server.server.impl;

import part2.Server.provider.ServiceProvider;
import part2.Server.server.RpcServer;
import part2.Server.server.work.WorkThread;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPoolRpcServer implements RpcServer {
    private ServiceProvider serviceProvider;
    private final ThreadPoolExecutor threadPool;

    // 创建默认参数线程池
    public ThreadPoolRpcServer(ServiceProvider serviceProvider) {
        this.serviceProvider = serviceProvider;
        threadPool =  new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(),
                    1000, 60, TimeUnit.SECONDS, new ArrayBlockingQueue<>(100));
    }

    // 创建自定义参数线程池
    public ThreadPoolRpcServer(ServiceProvider serviceProvider,
                               int corePoolSize,
                               int maximumPoolSize,
                               long keepAliveTime,
                               TimeUnit unit,
                               BlockingQueue<Runnable> workQueue
                               ) {
        threadPool = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
        this.serviceProvider = serviceProvider;
    }

    @Override
    public void start(int port) {
        System.out.println("服务端启动了...");
        try {
            ServerSocket serverSocket = new ServerSocket();
            while (true) {
                Socket socket = serverSocket.accept();
                threadPool.execute(new WorkThread(socket, serviceProvider));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stop() {

    }
}
