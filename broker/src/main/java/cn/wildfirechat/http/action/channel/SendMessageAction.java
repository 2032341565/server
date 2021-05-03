/*
 * This file is part of the Wildfire Chat package.
 * (c) Heavyrain2012 <heavyrain.lee@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package cn.wildfirechat.http.action.channel;


import cn.wildfirechat.http.annotation.HttpMethod;
import cn.wildfirechat.http.annotation.Route;
import cn.wildfirechat.http.handler.Response;
import cn.wildfirechat.common.APIPath;
import cn.wildfirechat.pojos.SendChannelMessageData;
import com.google.gson.Gson;
import cn.wildfirechat.http.RestResult;
import cn.wildfirechat.http.handler.Request;
import cn.wildfirechat.pojos.Conversation;
import cn.wildfirechat.pojos.SendMessageData;
import cn.wildfirechat.pojos.SendMessageResult;
import cn.wildfirechat.im.persistence.RPCCenter;
import cn.wildfirechat.im.persistence.TargetEntry;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import cn.wildfirechat.common.ErrorCode;
import cn.wildfirechat.util.IMTopic;

import java.util.concurrent.Executor;

import static cn.wildfirechat.proto.ProtoConstants.ConversationType.ConversationType_Channel;

@Route(APIPath.Channel_Message_Send)
@HttpMethod("POST")
public class SendMessageAction extends ChannelAction {

    @Override
    public boolean isTransactionAction() {
        return true;
    }

    @Override
    public boolean action(Request request, Response response) {
        if (request.getNettyRequest() instanceof FullHttpRequest) {
            SendChannelMessageData sendChannelMessageData = getRequestBody(request.getNettyRequest(), SendChannelMessageData.class);
            SendMessageData sendMessageData = new SendMessageData();
            sendMessageData.setConv(new Conversation());
            sendMessageData.getConv().setType(ConversationType_Channel);
            sendMessageData.getConv().setTarget(channelInfo.getTargetId());
            sendMessageData.getConv().setLine(sendChannelMessageData.getLine());
            sendMessageData.setSender(channelInfo.getOwner());
            sendMessageData.setPayload(sendChannelMessageData.getPayload());
            sendMessageData.setToUsers(sendChannelMessageData.getTargets());
            if (SendMessageData.isValide(sendMessageData)) {
                RPCCenter.getInstance().sendRequest(sendMessageData.getSender(), null, IMTopic.SendMessageTopic, sendMessageData.toProtoMessage().toByteArray(), sendMessageData.getSender(), TargetEntry.Type.TARGET_TYPE_USER, new RPCCenter.Callback() {
                    @Override
                    public void onSuccess(byte[] result) {
                        ByteBuf byteBuf = Unpooled.buffer();
                        byteBuf.writeBytes(result);
                        ErrorCode errorCode = ErrorCode.fromCode(byteBuf.readByte());
                        if (errorCode == ErrorCode.ERROR_CODE_SUCCESS) {
                            long messageId = byteBuf.readLong();
                            long timestamp = byteBuf.readLong();
                            sendResponse(response, null, new SendMessageResult(messageId, timestamp));
                        } else {
                            sendResponse(response, errorCode, null);
                        }
                    }

                    @Override
                    public void onError(ErrorCode errorCode) {
                        sendResponse(response, errorCode, null);
                    }

                    @Override
                    public void onTimeout() {
                        sendResponse(response, ErrorCode.ERROR_CODE_TIMEOUT, null);
                    }

                    @Override
                    public Executor getResponseExecutor() {
                        return command -> {
                            ctx.executor().execute(command);
                        };
                    }
                }, false);
                return false;
            } else {
                response.setStatus(HttpResponseStatus.OK);
                RestResult result = RestResult.resultOf(ErrorCode.INVALID_PARAMETER);
                response.setContent(new Gson().toJson(result));
            }
        }
        return true;
    }
}
