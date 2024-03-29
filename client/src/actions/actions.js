export const FETCH_TASK_SUMMARY_REQUEST = 'FETCH_TASK_SUMMARY_REQUEST';
export const FETCH_TASK_SUMMARY_SUCCESS = 'FETCH_TASK_SUMMARY_SUCCESS';
export const FETCH_TASK_SUMMARY_FAILURE = 'FETCH_TASK_SUMMARY_FAILURE';

export const FETCH_TASKS = 'FETCH_TASKS';


export const fetchTaskSummaryRequest = () => ({
    type: FETCH_TASK_SUMMARY_REQUEST
});

export const fetchTaskSummarySuccess = (data) => ({
    type: FETCH_TASK_SUMMARY_SUCCESS,
    payload: data
});

export const fetchTaskSummaryFailure = (error) => ({
    type: FETCH_TASK_SUMMARY_FAILURE,
    payload: error
});

export const fetchTheTasks = (data) => ({
    type: FETCH_TASKS,
    payload: data
})