package com.jthink.skyeye.trace.generater;

import com.jthink.skyeye.base.constant.Constants;
import com.jthink.skyeye.trace.dto.RegisterDto;
import org.I0Itec.zkclient.ZkClient;
import org.apache.zookeeper.data.Stat;

/**
 * JThink@JThink
 *
 * @author JThink
 * @version 0.0.1
 * @desc 自增长ID生成器，用来给app和host的服务进行编号（利用zk节点的版本号每写一次就自增的机制来实现）
 * @date 2017-03-24 11:25:31
 */
public class IncrementIdGen implements IdGen {

    // zk client
    private ZkClient zkClient;
    // register info
    private RegisterDto registerDto;

    /**
     * 利用zookeeper
     * @return
     */
    @Override
    public String nextId() {
        String app = this.registerDto.getApp();
        String host = this.registerDto.getHost();
        String path = Constants.ZK_REGISTRY_ID_ROOT_PATH + Constants.SLASH + app + Constants.SLASH + host;
        if (this.zkClient.exists(path)) {
            // 如果已经有该节点，表示已经为当前的host上部署的该app分配的编号（应对某个服务重启之后编号不变的问题），直接获取该id，而无需生成
            return this.zkClient.readData(Constants.ZK_REGISTRY_ID_ROOT_PATH + Constants.SLASH + app + Constants.SLASH + host);
        } else {
            // 节点不存在，那么需要生成id，利用zk节点的版本号每写一次就自增的机制来实现
            Stat stat = zkClient.writeDataReturnStat(Constants.ZK_REGISTRY_SEQ, new byte[0], -1);
            // 生成id
            String id = String.valueOf(stat.getVersion());
            // 将数据写入节点
            this.zkClient.createPersistent(path, true);
            this.zkClient.writeData(path, id);
            return id;
        }
    }

    public IncrementIdGen() {

    }

    public IncrementIdGen(ZkClient zkClient, RegisterDto registerDto) {
        this.zkClient = zkClient;
        this.registerDto = registerDto;
    }

    public ZkClient getZkClient() {
        return zkClient;
    }

    public IncrementIdGen setZkClient(ZkClient zkClient) {
        this.zkClient = zkClient;
        return this;
    }

    public RegisterDto getRegisterDto() {
        return registerDto;
    }

    public IncrementIdGen setRegisterDto(RegisterDto registerDto) {
        this.registerDto = registerDto;
        return this;
    }
}
