import {FETCH_TASK_SUMMARY_SUCCESS, FETCH_TASK_SUMMARY_FAILURE, FETCH_TASK_SUMMARY_REQUEST} from "../actions/actions";

const initialState = {
    taskSummary: [],
    loading: false,
    error: null
}

export const taskSummaryReducers = (state = initialState, action) => {
    switch (action.type) {
        case FETCH_TASK_SUMMARY_REQUEST:
            return {
                ...state,
                loading: true,
                error: null
        };
        case FETCH_TASK_SUMMARY_SUCCESS:
            return {
                ...state,
                taskSummary: action.payload,
                loading: false
            };
        case FETCH_TASK_SUMMARY_FAILURE:
            return {
                ...state,
                loading: false,
                error: action.payload
            };
        default:
            return state;
    }
}