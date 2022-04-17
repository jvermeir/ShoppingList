import {KotlinTodo} from '@myorg/data';
import './todos.module.css';

export interface KotlinTodosProps {
  todos: KotlinTodo[];
}

export function KotlinTodos(props: KotlinTodosProps) {
  return (
    <ul>
      {props.todos.map((t) => (
        <li className={'todo'} key={t.id}>{t.text} ({t.id})</li>
      ))}
    </ul>
  );
}

export default KotlinTodos;
