package part1.Server.server.work;

import lombok.AllArgsConstructor;
import part1.Server.provider.ServiceProvider;
import part1.common.Message.RpcRequest;
import part1.common.Message.RpcResponse;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.Socket;
import java.lang.reflect.Method;

@AllArgsConstructor
public class WorkThread implements Runnable{
    private Socket socket;
    private ServiceProvider serviceProvider;

    @Override
    public void run() {
        try {
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());

            // 读取客户端传过来的request
            RpcRequest rpcRequest = (RpcRequest) ois.readObject();
            // 反射调用服务方法获取返回值
            RpcResponse rpcResponse = getResponse(rpcRequest);
            // 向客户端写入response
            oos.writeObject(rpcResponse);
            oos.flush();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    // 根据远程请求，执行具体的server端本地服务
    private RpcResponse getResponse(RpcRequest rpcRequest) {
        // 得到服务名
        String interfaceName = rpcRequest.getInterfaceName();
        // 找到服务端相应服务实现类
        Object service = serviceProvider.getService(interfaceName);
        // 反射调用方法
        Method method = null;
        try {
            method = service.getClass().getMethod(rpcRequest.getMethodName(), rpcRequest.getParamsType());
            Object message = method.invoke(service, rpcRequest.getParams());
            return RpcResponse.sussess(message);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            System.out.println("方法执行错误...");
            return RpcResponse.fail();
        }
    }
}
