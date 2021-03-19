import React, {useState} from 'react';
import Autocomplete from "react-autocomplete";

export function RecipeSelector({id, allRecipes, theItem, updateMenu}) {
    const [menuItem, setMenuItem] = useState(theItem);
    const [recipes] = useState(allRecipes);

    function shouldItemRender(state, val) {
        return (
            state.toLowerCase().indexOf(val.toLowerCase()) !== -1
        );
    }

    return (
        <div className="autocomplete-wrapper">
            <Autocomplete
                key={id}
                value={menuItem.recipe}
                getItemValue={item => item}
                shouldItemRender={shouldItemRender}
                items={recipes}
                    renderItem={(item, isHighlighted) =>
                        <div key={item} className={`item ${isHighlighted ? 'selected-item' : ''}`}>
                            {item}
                        </div>
                    }
                    renderMenu={item => (
                        <div className="dropdown">
                            {item}
                        </div>
                    )}
                    onChange={(event, val) => {
                        setMenuItem({...menuItem, recipe: val});
                    }
                    }
                    onSelect={val => {
                        setMenuItem({...menuItem, recipe: val});
                        updateMenu(val, theItem)
                    }}
                />
        </div>
    )
}

export default RecipeSelector;
