/*
 * This file is part of the Wildfire Chat package.
 * (c) Heavyrain2012 <heavyrain.lee@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package cn.wildfirechat.http.action.admin;


import cn.wildfirechat.http.RestResult;
import cn.wildfirechat.http.annotation.HttpMethod;
import cn.wildfirechat.http.annotation.Route;
import cn.wildfirechat.http.handler.Request;
import cn.wildfirechat.http.handler.Response;
import cn.wildfirechat.common.APIPath;
import com.google.gson.Gson;
import cn.wildfirechat.pojos.InputCreateGroup;
import cn.wildfirechat.pojos.OutputCreateGroupResult;
import cn.wildfirechat.im.persistence.RPCCenter;
import cn.wildfirechat.im.persistence.TargetEntry;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import cn.wildfirechat.common.ErrorCode;
import cn.wildfirechat.util.IMTopic;

import java.util.concurrent.Executor;

@Route(APIPath.Create_Group)
@HttpMethod("POST")
public class CreateGroupAction extends AdminAction {

    @Override
    public boolean isTransactionAction() {
        return true;
    }

    @Override
    public boolean action(Request request, Response response) {
        if (request.getNettyRequest() instanceof FullHttpRequest) {
            InputCreateGroup inputCreateGroup = getRequestBody(request.getNettyRequest(), InputCreateGroup.class);
            if (inputCreateGroup.isValide()) {
                RPCCenter.getInstance().sendRequest(inputCreateGroup.getOperator(), null, IMTopic.CreateGroupTopic, inputCreateGroup.toProtoGroupRequest().toByteArray(), inputCreateGroup.getOperator(), TargetEntry.Type.TARGET_TYPE_USER, new RPCCenter.Callback() {
                    @Override
                    public void onSuccess(byte[] result) {
                        ByteBuf byteBuf = Unpooled.buffer();
                        byteBuf.writeBytes(result);
                        ErrorCode errorCode = ErrorCode.fromCode(byteBuf.readByte());
                        if (errorCode == ErrorCode.ERROR_CODE_SUCCESS) {
                            byte[] data = new byte[byteBuf.readableBytes()];
                            byteBuf.readBytes(data);
                            String groupId = new String(data);
                            sendResponse(response, null, new OutputCreateGroupResult(groupId));
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
                }, true);
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