/*
 * This file is part of the Wildfire Chat package.
 * (c) Heavyrain2012 <heavyrain.lee@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package cn.wildfirechat.http.action.admin;

import cn.wildfirechat.http.annotation.HttpMethod;
import cn.wildfirechat.http.annotation.Route;
import cn.wildfirechat.common.APIPath;
import com.google.gson.Gson;
import cn.wildfirechat.http.RestResult;
import cn.wildfirechat.http.handler.Request;
import cn.wildfirechat.http.handler.Response;
import cn.wildfirechat.pojos.InputOutputUserBlockStatus;
import cn.wildfirechat.im.persistence.RPCCenter;
import cn.wildfirechat.im.persistence.TargetEntry;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.util.internal.StringUtil;
import cn.wildfirechat.common.ErrorCode;

@Route(APIPath.User_Update_Block_Status)
@HttpMethod("POST")
public class BlockUserAction extends AdminAction {

    @Override
    public boolean isTransactionAction() {
        return true;
    }

    @Override
    public boolean action(Request request, Response response) {
        if (request.getNettyRequest() instanceof FullHttpRequest) {
            InputOutputUserBlockStatus inputUserBlock = getRequestBody(request.getNettyRequest(), InputOutputUserBlockStatus.class);
            if (inputUserBlock != null
                && !StringUtil.isNullOrEmpty(inputUserBlock.getUserId())) {

                ErrorCode errorCode = messagesStore.modifyUserStatus(inputUserBlock.getUserId(), inputUserBlock.getStatus());
                response.setStatus(HttpResponseStatus.OK);
                RestResult result;
                result = RestResult.resultOf(errorCode);
                response.setContent(new Gson().toJson(result));

                if (inputUserBlock.getStatus() == 2) {
                    RPCCenter.getInstance().sendRequest(null, null, RPCCenter.KICKOFF_USER_REQUEST, inputUserBlock.getUserId().getBytes(), inputUserBlock.getUserId(), TargetEntry.Type.TARGET_TYPE_USER, null, true);
                }

                sendResponse(response, ErrorCode.ERROR_CODE_SUCCESS, null);
            } else {
                response.setStatus(HttpResponseStatus.OK);
                RestResult result = RestResult.resultOf(ErrorCode.INVALID_PARAMETER);
                response.setContent(new Gson().toJson(result));
            }

        }
        return true;
    }
}
