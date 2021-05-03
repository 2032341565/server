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
import cn.wildfirechat.http.handler.Request;
import cn.wildfirechat.http.handler.Response;
import cn.wildfirechat.common.APIPath;
import cn.wildfirechat.pojos.InputOutputSensitiveWords;
import io.netty.handler.codec.http.FullHttpRequest;
import cn.wildfirechat.common.ErrorCode;

import java.util.List;

@Route(APIPath.Sensitive_Query)
@HttpMethod("POST")
public class SensitiveWordQueryAction extends AdminAction {

    @Override
    public boolean isTransactionAction() {
        return true;
    }

    @Override
    public boolean action(Request request, Response response) {
        if (request.getNettyRequest() instanceof FullHttpRequest) {
                List<String> words = messagesStore.getAllSensitiveWords();
                InputOutputSensitiveWords out = new InputOutputSensitiveWords();
                out.setWords(words);
                sendResponse(response, ErrorCode.ERROR_CODE_SUCCESS, out);
        }
        return true;
    }
}
