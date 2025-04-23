package part1.Server;

import part1.Server.provider.ServiceProvider;
import part1.Server.server.impl.SimpleRpcServer;
import part1.common.service.Impl.UserServiceImpl;

public class TestServer {
    public static void main(String[] args) {
        UserServiceImpl userService = new UserServiceImpl();
        // 服务端生成本地服务
        ServiceProvider serviceProvider = new ServiceProvider();
        serviceProvider.provideServiceInterface(userService);  // 本地服务注册

        SimpleRpcServer rpcServer = new SimpleRpcServer(serviceProvider);
        rpcServer.start(9999);  // 轮询
    }
}
