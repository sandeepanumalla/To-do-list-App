import React from 'react';
import { Route, BrowserRouter as Router, Routes } from 'react-router-dom';
import ContextProvider from './context/ContextProvider';
import Login from './pages/auth/Login';
import Register from './pages/auth/Register';
import AssignedTo from './pages/tasks/AssignedTo';
import Home from './pages/tasks/Home';
import Important from './pages/tasks/Important';
import MyDay from './pages/tasks/MyDay';
import Planned from './pages/tasks/Planned';
import Tasks from './pages/tasks/Tasks';
import Testing from './pages/tasks/Testing';
import { Provider } from 'react-redux';
import { store } from './store/store';

function App() {
    return (
        <Provider store={store}>
            <ContextProvider>
                <div className="App">
                    <Router>
                        <Routes>
                            <Route element={<Home />} path='/home' />
                            <Route element={<MyDay />} path='/my-day' />
                            <Route element={<Planned />} path='/planned' />
                            <Route element={<Important />} path='/important' />
                            <Route element={<AssignedTo />} path='/assigned-to' />
                            <Route element={<Tasks />} path='/tasks' />
                            <Route element={<Login />} path='/login' />
                            <Route element={<Register />} path='/register' />
                            <Route element={<Testing />} path='/testing' />
                        </Routes>
                    </Router>
                </div>
            </ContextProvider>
        </Provider>
    );
}

export default App;
