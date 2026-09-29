import { toast } from "react-toastify";
import { useEffect, useState } from "react";
import { createTask, retrieveTaskById, updateTask } from "../service/TaskApiService";
import { Container, Row, Col, Form, Button, Card } from "react-bootstrap";
import { useNavigate, useParams } from "react-router-dom";
import { FaTasks } from "react-icons/fa";

const AddTaskComponent = ({ userId }) => {
    const [task, setTask] = useState("");
    const [details, setDetails] = useState("");
    const [saving, setSaving] = useState(false);
    const { id } = useParams();
    const [loading, setLoading] = useState(Boolean(id));
    const [loadError, setLoadError] = useState(false);
    const navigate = useNavigate();
    const [errors, setErrors] = useState({ task: "" });
  
    useEffect(() => {
      let active = true;
      setLoading(Boolean(id));
      setLoadError(false);
      if (id) {
        retrieveTaskById(id).then(response => {
          if (active) {
            setDetails(response.data.object.details || "");
            setTask(response.data.object.task);
          }
        }).catch(() => {
          if (active) {
            setLoadError(true);
            toast.error("Could not load this task. Return to the task list and try again.");
          }
        }).finally(() => { if (active) setLoading(false); });
      } else {
        setTask("");
        setDetails("");
      }
      return () => { active = false; };
    }, [id]);

    async function saveTask(event) {
      event.preventDefault();
      if (loading || loadError || saving || !validateForm()) return;
      setSaving(true);
      try {
        const taskObj = { task, details };
        if (id) await updateTask(taskObj, id);
        else await createTask(taskObj, userId);
        navigate("/tasks");
      } catch (error) {
        toast.error(error.response?.data?.message || "Could not save task. Check that the backend is running.");
      } finally {
        setSaving(false);
      }
    }

  function validateForm() {
    let valid = true;
    const errorsCopy = { ...errors };
    if (task.trim()) {
      errorsCopy.task = "";
    } else {
      errorsCopy.task = "Task field is required";
      valid = false;
    }
    setErrors(errorsCopy);
    return valid;
  }

  function AddUpdateText() {
    if (id) {
      return "Update";
    } else {
      return "Add";
    }
  }

  return (
    <div className="d-flex justify-content-center align-items-center min-vh-100 bg-light">
      <Container>
        <Row className="justify-content-center">
          <Col md={8} lg={6} xl={5}>
            <Card className="shadow rounded-lg">
              <Card.Body>
                <div className="d-flex align-items-center mb-4">
                  <FaTasks className="mr-3 text-primary" size={32} />
                  <h2 className="m-0">{AddUpdateText()} Task</h2>
                </div>
                <Form onSubmit={saveTask}>
                  <Form.Group controlId="formTask">
                    <Form.Label>Task Description</Form.Label>
                    <Form.Control
                      as="textarea"
                      rows={3}
                      disabled={loading || loadError}
                      placeholder="Enter task description"
                      value={task}
                      onChange={(event) => setTask(event.target.value)}
                      isInvalid={!!errors.task}
                      className="rounded-lg"
                    />
                    <Form.Control.Feedback type="invalid" className="d-block">
                      {errors.task}
                    </Form.Control.Feedback>
                  </Form.Group>
                  <Button
                    variant="primary"
                    type="submit"
                    disabled={saving || loading || loadError}
                    className="mt-3 w-100 rounded-pill"
                  >
                    {AddUpdateText()} Task
                  </Button>
                </Form>
              </Card.Body>
            </Card>
          </Col>
        </Row>
      </Container>
    </div>
  );
};

export default AddTaskComponent;