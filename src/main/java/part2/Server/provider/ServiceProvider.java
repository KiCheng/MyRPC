package part2.Server.provider;

import java.util.HashMap;
import java.util.Map;

/**
 * 本地服务存放器
 */
public class ServiceProvider {
    // 集合中存放服务的实例
    private Map<String, Object> interfaceProvider;

    public ServiceProvider() {
        this.interfaceProvider = new HashMap<>();
    }

    // 本地注册服务
    public void provideServiceInterface(Object service) {
        String serviceName = service.getClass().getName();
        Class<?>[] interfaceName = service.getClass().getInterfaces();

        for(Class<?> clazz: interfaceName) {
            interfaceProvider.put(clazz.getName(), service);
        }
    }

    // 获取服务实例
    public Object getService(String interfaceName) {
        return interfaceProvider.get(interfaceName);
    }


}
