import { Todo } from '@myorg/data';
import './todos.module.css';

export interface TodosProps {
  todos: Todo[];
}

export function Todos(props: TodosProps) {
  return (
    <ul>
      {props.todos.map((t) => (
        <li className={'todo'} key={t.title}>{t.title}</li>
      ))}
    </ul>
  );
}

export default Todos;
