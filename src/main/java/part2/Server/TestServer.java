package part2.Server;

import part2.Server.provider.ServiceProvider;
import part2.Server.server.RpcServer;
import part2.Server.server.impl.NettyRpcServer;
import part2.Server.server.impl.SimpleRpcServer;
import part2.common.service.Impl.UserServiceImpl;
import part2.common.service.UserService;

public class TestServer {
    public static void main(String[] args) {
        UserService userService = new UserServiceImpl();
        // 服务端生成本地服务
        ServiceProvider serviceProvider = new ServiceProvider();
        serviceProvider.provideServiceInterface(userService);  // 本地服务注册

//        SimpleRpcServer rpcServer = new SimpleRpcServer(serviceProvider);
        RpcServer rpcServer = new NettyRpcServer(serviceProvider);
        rpcServer.start(9999);  // 轮询
    }
}
