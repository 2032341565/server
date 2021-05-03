package cn.wildfirechat.util.extended.mqttmessage;

import io.netty.buffer.ByteBuf;
import cn.wildfirechat.mqtt.MqttFixedHeader;
import cn.wildfirechat.mqtt.MqttMessage;
import cn.wildfirechat.mqtt.MqttMessageIdVariableHeader;

public class ModifiedMqttPubAckMessage extends MqttMessage {
    public ModifiedMqttPubAckMessage(MqttFixedHeader mqttFixedHeader, MqttMessageIdVariableHeader variableHeader, ByteBuf payload) {
        super(mqttFixedHeader, variableHeader, payload);
    }

    @Override
    public MqttMessageIdVariableHeader variableHeader() {
        return (MqttMessageIdVariableHeader) super.variableHeader();
    }
}
