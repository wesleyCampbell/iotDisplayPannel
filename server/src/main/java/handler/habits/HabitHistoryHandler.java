package handler.habits;

import handler.*;

import service.habits.*;

import dataaccess.exception.*;
import dataaccess.habits.*;

import com.google.protobuf.InvalidProtocolBufferException;

import api.habits.*;

import io.javalin.http.Context;

public class HabitHistoryHandler extends Handler {
	//
	// ================ CONSTRUCTORS ===================
	//
	
	private CreateHabitHistoryService createService;
	private DeleteHabitHistoryService deleteService;
	private GetHabitHistoryService getService;
	private MarkHabitCompletionService markCompleteService;
	private UpdateHabitHistoryNotesService updateNotesService;

	public HabitHistoryHandler(HabitsDatabaseManager db) {
		super();

		HistoryDAO dao = db.getHistoryDAO();

		this.createService = new CreateHabitHistoryService(dao);
		this.deleteService = new DeleteHabitHistoryService(dao);
		this.getService = new GetHabitHistoryService(dao);
		this.markCompleteService = new MarkHabitCompletionService(dao);
		this.updateNotesService = new UpdateHabitHistoryNotesService(dao);
	}

	//
	// ================= REQUEST METHODS =========================
	//
	
	/**
	 * Translates an HTTP request into an API request to
	 * create a new Habit History entry in the database.
	 *
	 * @param ctx The Javalin HTTP context
	 *
	 * @return True if API call is successful, false otherwise
	 */
	public boolean createHistoryRequest(Context ctx) {
		// Extract the data
		byte[] payload = ctx.bodyAsBytes();

		// Parse the API request and forward it to the relevant function
		CreateHabitHistoryAPIRequest request;
		try {
			request = CreateHabitHistoryAPIRequest.parseFrom(payload);
		} catch (InvalidProtocolBufferException ex) {
			this.setCtxStatus(ctx, HTTP_CODE_MALFORMED_REQUEST);
			return false;
		}

		switch(request.getPayloadCase()) {
			case CREATE_HISTORY:
				return this.createHistory(ctx, request.getCreateHistory());
			case CREATE_HISTORY_DATED:
				return this.createHistoryDated(ctx, request.getCreateHistoryDated());
			default:
				this.setCtxStatus(ctx, HTTP_CODE_MALFORMED_REQUEST);
				return false;
		}
	}

	/**
	 * Performs the API request to create a habit history entry in the database
	 * and translates the API response into an HTTP response.
	 *
	 * @param ctx The Javalin HTTP context
	 * @param request The API request to make
	 *
	 * @return True if API call is successful, false otherwise
	 */
	private boolean createHistory(Context ctx, CreateHabitHistoryRequest request) {
		CreateHabitHistoryResponse response;
		try {
			response = this.createService.createHistory(request);
		} catch (DataAccessException ex) {
			this.setCtxStatus(ctx, HTTP_CODE_INT_ERROR);
			return false;
		}

		this.formatCtx(ctx, response.toByteArray());

		return true;
	}

	/**
	 * Performs the API request to create a habit history entry with a date 
	 * in the database and translates the API response into an HTTP response.
	 *
	 * @param ctx The Javalin HTTP context
	 * @param request The API request to make
	 *
	 * @return True if API call is successful, false otherwise
	 */
	private boolean createHistoryDated(Context ctx, CreateHabitHistoryDatedRequest request) {
		CreateHabitHistoryResponse response;
		try {
			response = this.createService.createHistoryDated(request);
		} catch (DataAccessException ex) {
			this.setCtxStatus(ctx, HTTP_CODE_INT_ERROR);
			return false;
		}

		this.formatCtx(ctx, response.toByteArray());

		return true;
	}

	/**
	 * Translates an HTTP request into an API request to
	 * delete Habit History entries from the database.
	 *
	 * @param ctx The Javalin HTTP context
	 *
	 * @return true if API call is successful, false otherwise
	 */
	public boolean deleteHistoryRequest(Context ctx) {
		// Extract the request body
		byte[] body = ctx.bodyAsBytes();

		// Parse the API request
		DeleteHabitHistoryAPIRequest request;
		try {
			request = DeleteHabitHistoryAPIRequest.parseFrom(body);
		} catch (InvalidProtocolBufferException ex) {
			this.setCtxStatus(ctx, HTTP_CODE_MALFORMED_REQUEST);
			return false;
		}

		// Make the API call for the correct request
		switch (request.getPayloadCase()) {
			case DELETE_HISTORY_ENTRY:
				return this.deleteHistoryEntry(ctx, request.getDeleteHistoryEntry());
			case DELETE_HABIT_HISTORY:
				return this.deleteHabitHistory(ctx, request.getDeleteHabitHistory());
			case DELETE_HISTORY_ENTRIES:
				return this.deleteHistoryEntries(ctx, request.getDeleteHistoryEntries());
			default:
				this.setCtxStatus(ctx, HTTP_CODE_MALFORMED_REQUEST);
				return false;
		}
	}

	/**
	 * Performs the API call to delete a Habit History entry
	 * from the database and translates the API response into
	 * an HTTP response.
	 *
	 * @param ctx The Javalin HTTP context
	 * @param request The API request to make
	 *
	 * @return true if API call successful, false otherwise
	 */
	private boolean deleteHistoryEntry(Context ctx, DeleteHistoryEntryRequest request) {
		DeleteHistoryEntryResponse response;
		try {
			response = this.deleteService.deleteHistoryEntry(request);
		} catch (ObjectNotFoundException ex) {
			this.setCtxStatus(ctx, HTTP_CODE_NO_EXIST);
			return false;
		} catch (DataAccessException ex) {
			this.setCtxStatus(ctx, HTTP_CODE_INT_ERROR);
			return false;
		}

		this.formatCtx(ctx, response.toByteArray());

		return true;
	}

	/**
	 * Performs the API call to delete all Habit History entries
	 * associated with a given habit from the database and will
	 * translate the API response into an HTTP response.
	 *
	 * @param ctx The Javalin HTTP context
	 * @param request The API request to make
	 *
	 * @return True if API call successfull, false otherwise
	 */
	private boolean deleteHabitHistory(Context ctx, DeleteHabitHistoryRequest request) {
		DeleteHabitHistoryResponse response;
		try {
			response = this.deleteService.deleteHabitHistory(request);
		} catch (ObjectNotFoundException ex) {
			this.setCtxStatus(ctx, HTTP_CODE_NO_EXIST, "Request Habit ID does not exist!");
			return false;
		} catch (DataAccessException ex) {
			this.setCtxStatus(ctx, HTTP_CODE_INT_ERROR);
			return false;
		}

		this.formatCtx(ctx, response.toByteArray());

		return true;
	}

	/**
	 * Performs the API call to delete all Habit History entries
	 * completed on a given date from the database. Will also
	 * translate the API response into an HTTP response.
	 *
	 * @param ctx The Javalin HTTP context
	 * @param request the API request to make
	 *
	 * @return True if API call successful, false otherwise
	 */
	private boolean deleteHistoryEntries(Context ctx, DeleteHistoryEntriesRequest request) {
		DeleteHistoryEntriesResponse response;
		try {
			response = this.deleteService.deleteHistoryEntries(request);
		} catch (DataAccessException ex) {
			this.setCtxStatus(ctx, HTTP_CODE_INT_ERROR);
			return false;
		}

		this.formatCtx(ctx, response.toByteArray());

		return true;
	}

	/**
	 * Translates an HTTP request into an API call to
	 * fetch Habit History entries from the Database.
	 *
	 * @param ctx The Javalin HTTP context
	 *
	 * @return True if API call is successful, false otherwise
	 */
	public boolean getHistoryRequest(Context ctx) {
		// extract the raw request body
		byte[] body = ctx.bodyAsBytes();

		// Parse the API req
		ListHabitHistoryAPIRequest req;
		try {
			req = ListHabitHistoryAPIRequest.parseFrom(body);
		} catch (InvalidProtocolBufferException ex) {
			this.setCtxStatus(ctx, HTTP_CODE_MALFORMED_REQUEST);
			return false;
		}

		// Make the API call
		switch (req.getPayloadCase()) {
			case GET_HISTORY_ENTRY:
				return this.getHistory(ctx, req.getGetHistoryEntry());
			case LIST_HISTORY_BY_DATE:
				return this.getHistoryByDate(ctx, req.getListHistoryByDate());
			case LIST_HISTORY_BY_HABIT:
				return this.getHistoryByHabit(ctx, req.getListHistoryByHabit());
			case LIST_COMPLETED_HISTORY_BY_DATE:
				return this.getCompHistoryByDate(
					ctx,
					req.getListCompletedHistoryByDate()
				);
			case LIST_FAILED_HISTORY_BY_DATE:
				return this.getFailHistoryByDate(
					ctx,
					req.getListFailedHistoryByDate()
				);
			case LIST_COMPLETED_HISTORY_BY_HABIT:
				return this.getCompHistoryByHabit(
					ctx,
					req.getListCompletedHistoryByHabit()
				);
			case LIST_FAILED_HISTORY_BY_HABIT:
				return this.getFailHistoryByHabit(
					ctx,
					req.getListFailedHistoryByHabit()
				);
			default:
				this.setCtxStatus(ctx, HTTP_CODE_MALFORMED_REQUEST);
				return false;
		}
	}

	/**
	 * Makes an API call to fetch a habit history entry from the database
	 * and translates the API request into an HTTP request.
	 *
	 * @param ctx The Javalin HTTP context
	 * @param request The API request to make
	 *
	 * @return True if API call is successful, false otherwise.
	 */
	private boolean getHistory(Context ctx, GetHabitHistoryRequest request) {
		GetHabitHistoryResponse response;
		try {
			response = this.getService.getHabitHistory(request);
		} catch (ObjectNotFoundException ex) {
			this.setCtxStatus(ctx, HTTP_CODE_NO_EXIST);
			return false;
		} catch (DataAccessException ex) {
			this.setCtxStatus(ctx, HTTP_CODE_INT_ERROR);
			return false;
		}

		this.formatCtx(ctx, response.toByteArray());

		return true;
	}

	/**
	 * Makes an API call to fetch all habit history entries from the database
	 * that match a given date
	 * and translates the API request into an HTTP request.
	 *
	 * @param ctx The Javalin HTTP context
	 * @param request The API request to make
	 *
	 * @return True if API call is successful, false otherwise.
	 */
	private boolean getHistoryByDate(Context ctx, ListHabitHistoryByDateRequest request) {
		ListHabitHistoryByDateResponse response;
		try {
			response = this.getService.getHabitHistoryByDate(request);
		} catch (DataAccessException ex) {
			this.setCtxStatus(ctx, HTTP_CODE_INT_ERROR);
			return false;
		}

		this.formatCtx(ctx, response.toByteArray());

		return false;

	}

	/**
	 * Makes an API call to fetch all habit history entries from the database
	 * that belong to a given habit id
	 * and translates the API request into an HTTP request.
	 *
	 * @param ctx The Javalin HTTP context
	 * @param request The API request to make
	 *
	 * @return True if API call is successful, false otherwise.
	 */
	private boolean getHistoryByHabit(Context ctx, ListHabitHistoryByHabitRequest request) {
		ListHabitHistoryByHabitResponse response;
		try {
			response = this.getService.getHabitHistoryByHabit(request);
		} catch (ObjectNotFoundException ex) {
			this.setCtxStatus(ctx, HTTP_CODE_NO_EXIST, "The requested Habit ID does not exist!");
			return false;
		} catch (DataAccessException ex) {
			this.setCtxStatus(ctx, HTTP_CODE_INT_ERROR);
			return false;
		}

		this.formatCtx(ctx, response.toByteArray());

		return true;
	}

	/**
	 * Makes an API call to fetch all habit history entries from the database
	 * that are completed on a given date
	 * and translates the API request into an HTTP request.
	 *
	 * @param ctx The Javalin HTTP context
	 * @param request The API request to make
	 *
	 * @return True if API call is successful, false otherwise.
	 */
	private boolean getCompHistoryByDate(Context ctx, ListCompletedHabitHistoryByDateRequest request) {
		ListCompletedHabitHistoryByDateResponse response;
		try {
			response = this.getService.getCompletedHistoryByDate(request);
		} catch (DataAccessException ex) {
			this.setCtxStatus(ctx, HTTP_CODE_INT_ERROR);
			return false;
		}

		this.formatCtx(ctx, response.toByteArray());

		return true;
	}

	/**
	 * Makes an API call to fetch all habit history entries from the database
	 * that were failed on a given date
	 * and translates the API request into an HTTP request.
	 *
	 * @param ctx The Javalin HTTP context
	 * @param request The API request to make
	 *
	 * @return True if API call is successful, false otherwise.
	 */
	private boolean getFailHistoryByDate(Context ctx, ListFailedHabitHistoryByDateRequest request) {
		ListFailedHabitHistoryByDateResponse response;
		try {
			response = this.getService.getFailedHistoryByDate(request);
		} catch (DataAccessException ex) {
			this.setCtxStatus(ctx, HTTP_CODE_INT_ERROR);
			return false;
		}

		this.formatCtx(ctx, response.toByteArray());

		return true;
	}

	/**
	 * Makes an API call to fetch all habit history entries from the database
	 * that were completed and belong to a given habit
	 * and translates the API request into an HTTP request.
	 *
	 * @param ctx The Javalin HTTP context
	 * @param request The API request to make
	 *
	 * @return True if API call is successful, false otherwise.
	 */
	private boolean getCompHistoryByHabit(Context ctx, ListCompletedHabitHistoryByHabitRequest request) {
		ListCompletedHabitHistoryByHabitResponse response;
		try {
			response = this.getService.getCompletedHistoryByHabit(request);
		} catch (ObjectNotFoundException ex) {
			this.setCtxStatus(ctx, HTTP_CODE_NO_EXIST);
			return false;
		} catch (DataAccessException ex) {
			this.setCtxStatus(ctx, HTTP_CODE_INT_ERROR);
			return false;
		}

		this.formatCtx(ctx, response.toByteArray());

		return true;
	}

	/**
	 * Makes an API call to fetch all habit history entries from the database
	 * that were failed that belong to a given habit
	 * and translates the API request into an HTTP request.
	 *
	 * @param ctx The Javalin HTTP context
	 * @param request The API request to make
	 *
	 * @return True if API call is successful, false otherwise.
	 */
	private boolean getFailHistoryByHabit(Context ctx, ListFailedHabitHistoryByHabitRequest request) {
		ListFailedHabitHistoryByHabitResponse response;
		try {
			response = this.getService.getFailedHistoryByHabit(request);
		} catch (ObjectNotFoundException ex) {
			this.setCtxStatus(ctx, HTTP_CODE_NO_EXIST);
			return false;
		} catch (DataAccessException ex) {
			this.setCtxStatus(ctx, HTTP_CODE_INT_ERROR);
			return false;
		}

		this.formatCtx(ctx, response.toByteArray());
		
		return true;
	}

	/**
	 * Translates an HTTP request into an API call that
	 * marks the completion status of a given history entry.
	 *
	 * @param ctx The HTTP Javalin context.
	 *
	 * @return True if API call is successful, false otherwise
	 */
	public boolean markHistoryCompletionStateRequest(Context ctx) {
		// Extract the raw request body
		byte[] body = ctx.bodyAsBytes();

		// Parse the API call
		MarkHistoryCompletionStateRequest request;
		try {
			request = MarkHistoryCompletionStateRequest.parseFrom(body);
		} catch (InvalidProtocolBufferException ex) {
			this.setCtxStatus(ctx, HTTP_CODE_MALFORMED_REQUEST);
			return false;
		}

		// Make the API call
		MarkHistoryCompletionStateResponse response;
		try {
			response = markCompleteService.setHistoryCompletionState(request);
		} catch (ObjectNotFoundException ex) {
			this.setCtxStatus(ctx, HTTP_CODE_NO_EXIST, "The requested History ID does not exist!");
			return false;
		} catch (DataAccessException ex) {
			this.setCtxStatus(ctx, HTTP_CODE_INT_ERROR);
			return false;
		}

		// format the response
		this.formatCtx(ctx, response.toByteArray());

		return true;
	}

	/**
	 * Translates an HTTP request into an API call that
	 * updates the notes of a given history id.
	 *
	 * @param ctx The Javalin HTTP context
	 *
	 * @return True if API call successful, false otherwise.
	 */
	public boolean updateHistoryNotesRequest(Context ctx) {
		// Extract the raw request body
		byte[] body = ctx.bodyAsBytes();

		// Parse the API request
		UpdateHabitHistoryNotesRequest request;
		try {
			request = UpdateHabitHistoryNotesRequest.parseFrom(body);
		} catch (InvalidProtocolBufferException ex) {
			this.setCtxStatus(ctx, HTTP_CODE_MALFORMED_REQUEST);
			return false;
		}

		// Make the API call
		UpdateHabitHistoryNotesResponse response;
		try {
			response = this.updateNotesService.updateHabitHistoryNotes(request);
		} catch (ObjectNotFoundException ex) {
			this.setCtxStatus(ctx, HTTP_CODE_NO_EXIST, "The requested History ID does not exist!");
			return false;
		} catch (DataAccessException ex) {
			this.setCtxStatus(ctx, HTTP_CODE_INT_ERROR);
			return false;
		}

		// Format the HTTP response with the results
		this.formatCtx(ctx, response.toByteArray());

		return true;
	}
}


