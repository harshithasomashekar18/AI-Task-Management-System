import HeaderComponent from "./component/HeaderComponent";
import { BrowserRouter, Navigate, Route, Routes } from "react-router-dom";
import CreateAccount from "./component/CreateAccount";
import LoginComponent from "./component/LoginComponent";
import { getLoggedInUserId, isUserLoggedIn } from "./service/AuthApiService";
import TasksComponent from "./component/TasksComponent";
import AddTaskComponent from "./component/AddTaskComponent";
import TaskHistory from "./component/TaskHistory";
import HomePage from "./component/Home";
import DetailPage from "./component/DetailPage";

import { ToastContainer } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';

function AuthenticatedRoute({ component: Component }) {
  return isUserLoggedIn() ? <Component userId={getLoggedInUserId()} /> : <Navigate to="/login" replace />;
}

function App() {
  return (
    <>
      <BrowserRouter>
      <ToastContainer position="bottom-right" />
        <HeaderComponent />
        <Routes>
          <Route path="/" element={<HomePage />} />
          <Route
            path="/tasks"
            element={
              <AuthenticatedRoute component={TasksComponent} />
            }
          />
          <Route
            path="/add-task"
            element={
              <AuthenticatedRoute component={AddTaskComponent} />
            }
          />

          <Route
           path="/task-details/:id" 
           element={
              <AuthenticatedRoute component={DetailPage} />
           }
              />

          <Route
            path="/history"
            element={
              <AuthenticatedRoute component={TaskHistory} />
            }
          />
          <Route
            path="/update-task/:id"
            element={
              <AuthenticatedRoute component={AddTaskComponent} />
            }
          />
          <Route path="/create-account" element={<CreateAccount />} />
          <Route path="/login" element={<LoginComponent />} />
        </Routes>
      </BrowserRouter>
    </>
  );
}

export default App;
