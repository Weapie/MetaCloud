/*
 * this class is by RauchigesEtwas
 */

package eu.metacloudservice.webserver.handel;

import eu.metacloudservice.Driver;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

public class RequestPUT {

    public void handle(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
        String uri = request.uri();
        if (uri.contains("/")) {
            String authenticatorKey = uri.split("/")[1];
            if (Driver.getInstance().getWebServer().AUTH_KEY.contains(authenticatorKey)){
                String path = uri.replace("/" + authenticatorKey, "");
                if (Driver.getInstance().getWebServer().getRoutes(path) == null){
                    FullHttpResponse response = createResponse(HttpResponseStatus.NOT_FOUND, "{\"reason\":\"please enter a right path\"}");
                    ctx.writeAndFlush(response);
                } else if (!path.isEmpty()){
                    ByteBuf content = request.content();
                    String payload = content.toString(CharsetUtil.UTF_8);
                    Driver.getInstance().getWebServer().updateRoute(path, payload);
                    FullHttpResponse response = createResponse(HttpResponseStatus.OK, "{\"reason\":\"data received\"}");
                    ctx.writeAndFlush(response);
                }else {
                    FullHttpResponse response = createResponse(HttpResponseStatus.NOT_FOUND, "{\"reason\":\"please enter a right path\"}");
                    ctx.writeAndFlush(response);
                }

            }else {
                FullHttpResponse response = createResponse(HttpResponseStatus.NOT_FOUND, "{\"reason\":\"please enter the right auth-key\"}");
                ctx.writeAndFlush(response);
            }
        } else {
            FullHttpResponse response = createResponse(HttpResponseStatus.NOT_FOUND, "{\"reason\":\"please enter the right auth-key\"}");
            ctx.writeAndFlush(response);
        }
    }

    private FullHttpResponse createResponse(HttpResponseStatus status, String content) {
        FullHttpResponse response = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1,
                status,
                Unpooled.copiedBuffer(content, CharsetUtil.UTF_8));

        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/json; charset=UTF-8");
        response.headers().set(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());
        return response;
    }
}