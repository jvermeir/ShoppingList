import { useEffect, useState } from 'react';
import { KotlinTodo } from '@myorg/data';
import { KotlinTodos } from '@myorg/ui';

const AppKotlin = () => {
  const [todos, setTodos] = useState<KotlinTodo[]>([]);

  useEffect(() => {
    fetch('/kotlin/todos')
      .then((_) => _.json())
      .then(setTodos);
  }, []);

  function addTodo() {
    const newTodo = {text:`New todo ${Math.floor(Math.random() * 1000)}`};
    fetch('/kotlin/todo', {
      method: 'POST',
      headers: {
        'content-type': 'application/json;charset=UTF-8',
      },
      body: JSON.stringify(newTodo)
    })
      .then((_) => _.json())
      .then((newTodo) => {
        setTodos([...todos, newTodo]);
      });
  }

  return (
    <>
      <h1>Todos</h1>
      <KotlinTodos todos={todos} />
      <button id={'add-todo'} onClick={addTodo}>
        Add Todo
      </button>
    </>
  );
};

export default AppKotlin;
