package part1.Server.server.impl;

import lombok.AllArgsConstructor;
import part1.Server.provider.ServiceProvider;
import part1.Server.server.RpcServer;
import part1.Server.server.work.WorkThread;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

@AllArgsConstructor
public class SimpleRpcServer implements RpcServer {

    private ServiceProvider serviceProvider;

    @Override
    public void start(int port) {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("服务器启动了...");
            while (true) {
                // 如果没有客户端连接，会一直阻塞
                Socket socket = serverSocket.accept();
                // 如果有连接，创建一个新的线程执行处理
                new Thread(new WorkThread(socket, serviceProvider)).start();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void stop() {

    }
}
