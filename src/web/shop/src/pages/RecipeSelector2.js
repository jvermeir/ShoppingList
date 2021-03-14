import React, {useState} from 'react';
import Autocomplete from "react-autocomplete";

export function RecipeSelector2({key, menuItems, allRecipes, theItem, updateMenu}) {
    console.log({theItem: theItem, allRecipes: allRecipes.length, menuItems: menuItems})
    const [menuItem, setMenuItem] = useState(theItem);
    const [recipes] = useState(allRecipes);

    function shouldItemRender(menuItem, val) {
        if (menuItem && val && val.recipe) {
            // console.log({val:val, menuItem:menuItem});
            return (
                val === "" || menuItem.toLowerCase().indexOf(val.recipe.toLowerCase()) !== -1
            )
        } else {
            console.log({val: val, menuItem: menuItem});
        }
    }



    return (
        <>
            {menuItem && recipes &&
            <div className="autocomplete-wrapper">
                <Autocomplete
                    key={menuItem.id}
                    value={menuItem}
                    items={recipes}
                    getItemValue={item => item}
                    // shouldItemRender={shouldItemRender}
                    renderMenu={item => (
                        <div className="dropdown">
                            {item.recipe}
                        </div>
                    )}
                    renderItem={(item, isHighlighted) =>
                        <div key={item} className={`item ${isHighlighted ? 'selected-item' : ''}`}>
                            {item.recipe}
                        </div>
                    }
                    onChange={(event, val) => {
                        console.log({onChange: val});
                        setMenuItem(val)
                    }
                    }
                    onSelect={val => {
                        console.log({onSelect: val});
                        setMenuItem(val)
                        updateMenu(val, theItem)
                    }}
                />
            </div>
            }
        </>
    );
}

export default RecipeSelector2;
