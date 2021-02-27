import React, {useState} from 'react';
import Autocomplete from "react-autocomplete";

export function RecipeSelector({recipeList, theMenuItem, updateMenu}) {
    const [menuItem, setMenuItem] = useState(theMenuItem.recipe);
    const [recipes] = useState(recipeList);

    function renderMenuItem(menuItem, val) {
        if (val === "" || menuItem.toLowerCase().indexOf(val.toLowerCase()) !== -1) {
            console.log({"event": "renderMenuItem, found match",menuItem, val});
        }
        return (
            val === "" || menuItem.toLowerCase().indexOf(val.toLowerCase()) !== -1
        );
    }

    return (
        <>
            {menuItem && recipes && <div className="autocomplete-wrapper">
                <Autocomplete
                    key={menuItem}
                    value={menuItem}
                    items={recipes}
                    getItemValue={menuItem => menuItem}
                    shouldItemRender={renderMenuItem}
                    renderMenu={menuItem => (
                        <div className="dropdown">
                            {menuItem}
                        </div>
                    )}
                    renderItem={(item, isHighlighted) =>
                        <div key={item} className={`item ${isHighlighted ? 'selected-item' : ''}`}>
                            {item}
                        </div>
                    }
                    onChange={(event, val) => {
                        setMenuItem(val);
                        console.log({"event":"onChange", "val":val});
                    }}
                    onSelect={(val) => {
                        console.log({"event":"onSelect", "val":val});
                        setMenuItem(val);
                        updateMenu(val, theMenuItem);
                    }}
                />
            </div>
            }
        </>
    );
}

export default RecipeSelector;
