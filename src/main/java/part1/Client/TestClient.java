package part1.Client;

import part1.Client.proxy.ClientProxy;
import part1.common.pojo.User;
import part1.common.service.UserService;

public class TestClient {
    public static void main(String[] args) {
        // 创建代理类，实现InvocationHandler接口
        ClientProxy clientProxy = new ClientProxy("127.0.0.1", 9999);
        // 创建代理对象
        UserService proxy = clientProxy.getProxy(UserService.class);

        User user = proxy.getUserByUserId(1);
        System.out.println("从服务端得到的user = " + user.toString());

        User u = User.builder().id(100).userName("KiCheng").sex(true).build();
        Integer id = proxy.insertUserId(u);
        System.out.println("向服务端插入user的id = " + id);

    }
}
