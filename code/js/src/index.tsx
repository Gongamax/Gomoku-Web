import * as React from 'react';
import {createRoot} from 'react-dom/client';
import {App} from './App';
import {getHome} from "./Service/GomokuService";

const root = createRoot(document.getElementById('main-div'));

export const linkRecipe = getHome()
    .then((data) => {
        return data.recipeLinks.map((link): {rel: string, href: string} => {
             return {
                "rel": getRelName(link.rel[0]),
                "href": link.href
            }
        });
    })

root.render(<App />);
function getRelName(RelUrl: string): string {
    return RelUrl.split("/").pop();
}