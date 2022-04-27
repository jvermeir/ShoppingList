// eslint-disable-next-line @typescript-eslint/no-unused-vars
import styles from './app.module.scss';
import {useEffect, useState} from 'react';

interface MenuItem {
  name: string;
}

export const App = () => {
  const [menuItems, setMenuItems] = useState<MenuItem[]>([]);

  useEffect(() => {
    fetch('/api/menuitems')
      .then((_) => _.json())
      .then(setMenuItems);
  }, []);

  function addMenuItem() {
    const newMenuItem = {name:`New menu item ${Math.floor(Math.random() * 1000)}`};
    fetch('/api/addMenuItem', {
      method: 'POST',
      body: JSON.stringify(newMenuItem),
    })
      .then((_) => _.json())
      .then((newMenuItem) => {
        setMenuItems([...menuItems, newMenuItem]);
      });
  }

  return (
    <>
      <h1>MenuItems</h1>
      <ul>
        {menuItems.map((t) => (
          <li className={'todo'}>{t.name}</li>
        ))}
      </ul>
      <button id={'add-menuItem'} onClick={addMenuItem}>Add MenuItem</button>
    </>
  );
};

export default App;
