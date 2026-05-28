package handler.habits;

import handler.*;

import service.habits.*;

import dataaccess.exception.*;
import dataaccess.habits.*;

import com.google.protobuf.InvalidProtocolBufferException;

import api.habits.*;

import io.javalin.http.Context;

public class HabitHandler extends Handler {
	//
	// ==================== CONSTRUCTORS ====================
	//
	
	private CreateHabitService createHabitService;
	private DeleteHabitService deleteHabitService;
	private ListHabitsService listHabitService;
	
	public HabitHandler(HabitsDatabaseManager dbManager) {
		super();

		HabitsDAO habitsDAO = dbManager.getHabitsDAO();

		this.createHabitService = new CreateHabitService(habitsDAO);
		this.deleteHabitService = new DeleteHabitService(habitsDAO);
		this.listHabitService = new ListHabitsService(habitsDAO);
	}

	//
	// ==================== REQUEST METHODS ==================
	//
	
	/**
	 * Translates a Javalin HTTP context into an API call to 
	 * create a new habit in the database
	 *
	 * @param ctx The Javalin HTTP context
	 *
	 * @return True if request is successful, false otherwise
	 */
	public boolean createHabitRequest(Context ctx) {
		// Get the raw request data
		byte[] body = ctx.bodyAsBytes();

		// Parse the request into an API call
		CreateHabitEntryRequest request;
		try {
			request = CreateHabitEntryRequest.parseFrom(body);
		} catch (InvalidProtocolBufferException ex) {
			this.setCtxStatus(ctx, HTTP_CODE_INT_ERROR);
			return false;
		}

		CreateHabitEntryResponse response;
		try {
			response = this.createHabitService.createHabit(request);
		} catch (DataAccessException ex) {
			this.setCtxStatus(ctx, HTTP_CODE_MALFORMED_REQUEST);
			return false;
		}
		
		// Format the HTTP response
		this.formatCtx(ctx, response.toByteArray());	

		return true;
	}

	/**
	 * Translates a Javalin HTTP context into an API call
	 * to delete a habit from the database.
	 *
	 * @param ctx The Javalin HTTP context
	 *
	 * @return True if request is successful, false otherwise
	 */
	public boolean deleteHabitRequest(Context ctx) {
		// get the raw request data
		byte[] body = ctx.bodyAsBytes();

		// Parse the request into the API call
		DeleteHabitEntryRequest request;
		try {
			request = DeleteHabitEntryRequest.parseFrom(body);
		} catch (InvalidProtocolBufferException ex) {
			this.setCtxStatus(ctx, HTTP_CODE_MALFORMED_REQUEST);
			return false;
		}

		// Make the API call
		DeleteHabitEntryResponse response;
		try {
			response = this.deleteHabitService.deleteHabit(request);
		} catch (ObjectNotFoundException ex) {
			this.setCtxStatus(ctx, HTTP_CODE_NO_EXIST);
			return false;
		} catch (DataAccessException ex) {
			this.setCtxStatus(ctx, HTTP_CODE_INT_ERROR);
			return false;
		}

		// Format the HTTP response
		this.formatCtx(ctx, response.toByteArray());

		return true;
	}

	/**
	 * Translates a Javalin HTTP context into an API call
	 * to list the habits in the database.
	 *
	 * @param ctx The Javalin HTTP context
	 *
	 * @return True if request is successful, false otherwise
	 */
	public boolean listHabitsRequest(Context ctx) {
		// Get the raw request data
		byte[] body = ctx.bodyAsBytes();

		// Parse the API request
		GetHabitAPIRequest requestAPI;
		try {
			requestAPI = GetHabitAPIRequest.parseFrom(body);
		} catch (InvalidProtocolBufferException ex) {
			this.setCtxStatus(ctx, HTTP_CODE_MALFORMED_REQUEST);
			return false;
		}

		switch(requestAPI.getPayloadCase()) {
			case GET_HABIT:
				return this.fetchHabit(ctx, requestAPI.getGetHabit());
			case GET_ALL_HABITS:
				return this.fetchAllHabits(ctx, requestAPI.getGetAllHabits());
			case GET_ACTIVE_HABITS:
				return this.fetchActiveHabits(ctx, requestAPI.getGetActiveHabits());
			case GET_INACTIVE_HABITS:
				return this.fetchInactiveHabits(ctx, requestAPI.getGetInactiveHabits());
			default:
				this.setCtxStatus(ctx, HTTP_CODE_MALFORMED_REQUEST);
				return false;
		}
	}

	/**
	 * Performs the API request to fetch one individual habit and translates
	 * the API response into an HTTP response.
	 *
	 * @param ctx The Javalin HTTP context
	 * @param request The API request to make.
	 *
	 * @return True if API call successful, false otherwise
	 */
	private boolean fetchHabit(Context ctx, GetHabitEntryRequest request) {
		GetHabitEntryResponse response;
		try {
			response = this.listHabitService.getHabitEntry(request);
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
	 * Performs the API request to fetch all habits in the database and
	 * translates the API response into an HTTP response
	 *
	 * @param ctx The Javalin HTTP context
	 * @param request The API request to make
	 *
	 * @return True if API call is succesfull, false otherwise
	 */
	private boolean fetchAllHabits(Context ctx, GetHabitCatalogRequest request) {
		GetHabitCatalogResponse response;
		try {
			response = this.listHabitService.getHabitCatalog(request);
		} catch (DataAccessException ex) {
			this.setCtxStatus(ctx, HTTP_CODE_INT_ERROR);
			return false;
		}

		// format the response
		this.formatCtx(ctx, response.toByteArray());
		
		return true;
	}

	/**
	 * Performs the API request to fetch all active habits from
	 * the database and translates the API response into an
	 * HTTP response.
	 *
	 * @param ctx The Javalin HTTP context
	 * @param request the API request to make
	 *
	 * @return True if API call is succesfull, false otherwise
	 */
	private boolean fetchActiveHabits(Context ctx, GetActiveHabitsRequest request) {
		GetActiveHabitsResponse response;
		try {
			response = this.listHabitService.getActiveHabits(request);
		} catch (DataAccessException ex) {
			this.setCtxStatus(ctx, HTTP_CODE_INT_ERROR);
			return false;
		}

		// format the response
		this.formatCtx(ctx, response.toByteArray());

		return true;
	}

	/**
	 * Performs the API request to fetch all inactive habits from
	 * the databbase and translates the API response into an
	 * HTTP response.
	 *
	 * @param ctx The Javalin HTTP context
	 * @param request The API request to make
	 *
	 * @return True if API call is succesfull, false otherwise
	 */
	private boolean fetchInactiveHabits(Context ctx, GetInactiveHabitsRequest request) {
		GetInactiveHabitsResponse response;
		try {
			response = this.listHabitService.getInactiveHabits(request);
		} catch (DataAccessException ex) {
			this.setCtxStatus(ctx, HTTP_CODE_INT_ERROR);
			return false;
		}

		this.formatCtx(ctx, response.toByteArray());

		return true;
	}
}
