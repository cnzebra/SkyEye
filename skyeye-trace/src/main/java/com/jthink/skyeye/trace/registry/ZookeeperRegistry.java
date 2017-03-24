package com.jthink.skyeye.trace.registry;

import com.jthink.skyeye.base.constant.Constants;
import com.jthink.skyeye.trace.dto.RegisterDto;
import com.jthink.skyeye.trace.generater.IdGen;
import com.jthink.skyeye.trace.generater.IncrementIdGen;
import org.I0Itec.zkclient.ZkClient;

/**
 * JThink@JThink
 *
 * @author JThink
 * @version 0.0.1
 * @desc 利用zookeeper实现注册中心
 * @date 2017-03-23 10:14:22
 */
public class ZookeeperRegistry implements Registry {

    /**
     * 向注册中心进行注册，生成该服务的编号并返回
     * @param registerDto
     * @return
     */
    @Override
    public String register(RegisterDto registerDto) {
        String host = registerDto.getHost();
        String app = registerDto.getApp();

        // 向注册中心注册
        ZkClient zkClient = new ZkClient(registerDto.getZkServers(), 60000, 5000);
        zkClient.createPersistent(Constants.ZK_REGISTRY_SERVICE_ROOT_PATH + Constants.SLASH + app, true);
        IdGen idGen = new IncrementIdGen(zkClient, registerDto);
        String id = idGen.nextId();
        zkClient.createEphemeral(Constants.ZK_REGISTRY_SERVICE_ROOT_PATH + Constants.SLASH + app + Constants.SLASH + host, id);

        return id;
    }

    public static void main(String[] args) {
        RegisterDto dto = new RegisterDto();
        dto.setHost("qianjc-pc").setZkServers("riot01:2181,riot02:2181,riot03:2181").setApp("app2");

        ZookeeperRegistry zookeeperRegistry = new ZookeeperRegistry();
        System.out.println(zookeeperRegistry.register(dto));
    }
}
