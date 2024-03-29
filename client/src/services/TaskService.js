import BaseURL, { axiosClient } from './commons/BaseURL';

import {
    fetchTaskSummaryRequest,
    fetchTaskSummarySuccess,
    fetchTaskSummaryFailure, fetchTheTasks
} from '../actions/actions';

export const fetchTaskSummary = () => {
    return async (dispatch) => {
        dispatch(fetchTaskSummaryRequest()); // Dispatch request action

        try {
            const response = await axiosClient.get(`${BaseURL}/tasks/summary`);
            dispatch(fetchTaskSummarySuccess(response.data)); // Dispatch success action with fetched data
        } catch (error) {
            dispatch(fetchTaskSummaryFailure(error)); // Dispatch failure action with error
            console.error("Error fetching task summary:", error);
        }
    };
};


export const fetchTasks = (queryParams) => {
    return async (dispatch) => {
        try {
            const url = new URL(`${BaseURL}/tasks`);
            Object.keys(queryParams).forEach(key => url.searchParams.append(key, queryParams[key]));
            const response = await axiosClient.get(url.toString());
            dispatch(fetchTheTasks(response.data));
        } catch (error) {
            dispatch(fetchTaskSummaryFailure(error)); // Dispatch failure action with error
            console.error("Error fetching tasks:", error);
        }
    }
}
