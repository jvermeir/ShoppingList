import React, {useState} from 'react';
import Autocomplete from "react-autocomplete";

export function RecipeSelector({recipeList, theMenuItem, updateMenu}) {
    const [menuItem, setMenuItem] = useState(theMenuItem.recipe);
    const [recipes] = useState(recipeList);

    function renderMenuItem(menuItem, val) {
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
                        // TODO: this event should not cause a rest call, so updateMenu should not be called
                        // if the field is emptied, it gets a default of -, this value is set automatically and should
                        // therefore trigger updateMenu(). this seems a bit complicated...
                        // now you can't empty the field because it will get a value of -
                        const finalRecipeName = val === "" ? "-" : val;
                        setMenuItem(finalRecipeName);
                        console.log({"event": "onChange", "val": finalRecipeName});
                        if (finalRecipeName === "-") {
                            updateMenu(finalRecipeName, theMenuItem);
                        }
                    }}
                    onSelect={(val) => {
                        console.log({"event": "onSelect", "val": val});
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
