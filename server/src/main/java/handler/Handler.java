package handler;

import api.messages.*;

import io.javalin.http.Context;

public abstract class Handler {
	//
	// ================ HTTP CODES ==================
	//
	
	protected static final int HTTP_CODE_OK = 200;
	protected static final int HTTP_CODE_MALFORMED_REQUEST = 400;
	protected static final int HTTP_CODE_UNAUTH = 401;
	protected static final int HTTP_CODE_TAKEN = 403;
	protected static final int HTTP_CODE_NO_EXIST = 404;
	protected static final int HTTP_CODE_INT_ERROR = 500;

	// 
	// =================== GENERIC HTTP ERROR MESSAGES 
	//

	protected static final String ERROR_MESSAGE_TEMPLATE = "Error: %s";
	
	protected static final String HTTP_ERROR_MSG = "System error.";
	protected static final String HTTP_UNAUTH_MSG = "Not authorized to access requested resource.";
	protected static final String HTTP_NO_EXIST_MSG = "Requested resource does not exist.";
	protected static final String HTTP_INT_ERROR_MSG = "Internal server error. Please try again later.";
	protected static final String HTTP_MALFORMED_REQUEST_MSG = "Malformed request from client.";

	//
	// ==================== GLOBALS =====================
	//
	
	protected static final String HTTP_CTX_TYPE = "application/x-protobuf";

	//
	// ================== MEMBER METHODS ================
	//
	
	/**
	 * Will take a given context and will set its status and error message,
	 * if an error code is provided. Note that in the case of HTTP_CODE_OK,
	 * the result will not be set as that is most often manual.
	 *
	 * @param ctx The HTTP Javalin context
	 * @param httpCode The HTTP code to set
	 * @param errorMsg The error message to imbed in the result
	 */
	protected void setCtxStatus(Context ctx, int httpCode, String errorMsg) {
		ctx.status(httpCode);

		ErrorMessage.Builder msgBuilder = ErrorMessage.newBuilder();	
		msgBuilder.setCode(httpCode);

		String msg = String.format(ERROR_MESSAGE_TEMPLATE, errorMsg);

		if (httpCode != HTTP_CODE_OK) {
			msgBuilder.setMsg(msg);
		}

		ctx.result(msgBuilder.build().toByteArray());
	}

	/**
	 * Will take a given context and will set its status and error message,
	 * if an error code is provided. Note that in the case of HTTP_CODE_OK,
	 * the result will not be set as that is most often manual.
	 *
	 * @param ctx The HTTP Javalin context
	 * @param httpCode The HTTP code to set
	 */
	protected void setCtxStatus(Context ctx, int httpCode) {
		String errorMsg;

		switch (httpCode) {
			case HTTP_CODE_OK:
				errorMsg = "";
				break;
			case HTTP_CODE_INT_ERROR:
				errorMsg = HTTP_INT_ERROR_MSG;
				break;
			case HTTP_CODE_UNAUTH:
				errorMsg = HTTP_UNAUTH_MSG;
				break;
			case HTTP_CODE_NO_EXIST:
				errorMsg = HTTP_NO_EXIST_MSG;
				break;
			case HTTP_CODE_MALFORMED_REQUEST:
				errorMsg = HTTP_MALFORMED_REQUEST_MSG;
				break;
			default:
				errorMsg = "Unknown internal error.";
				break;
		}

		this.setCtxStatus(ctx, httpCode, errorMsg);
	}

	/**
	 * Will set the given context to the correct protobuf type.
	 *
	 * @param ctx The Javalin HTTP context to configure
	 * @param result A byte array to include as the HTTP response. 
	 */
	protected void formatCtx(Context ctx, byte[] result) {
		this.setCtxStatus(ctx, HTTP_CODE_OK);
		ctx.contentType(HTTP_CTX_TYPE);
		ctx.result(result);
	}
}

