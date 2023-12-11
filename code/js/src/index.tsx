import * as React from 'react';
import {createRoot} from 'react-dom/client';
import {App} from './App';
import {getHome} from "./Service/GomokuService";

/**
 * `root` is a root node for the React application.
 * It is created by calling `createRoot` on a DOM element with the id 'main-div'.
 */
const root = createRoot(document.getElementById('main-div'));

/**
 * `linkRecipe` is a Promise that resolves to an array of objects representing links.
 * Each link object has a 'rel' property and a 'href' property.
 * The 'rel' property is the name of the relation, which is obtained by calling `getRelName` on the first relation of the link.
 * The 'href' property is the href of the link.
 */
export const linkRecipe = getHome()
    .then((data) => {
        return data.recipeLinks.map((link): {rel: string, href: string} => {
             return {
                "rel": getRelName(link.rel[0]),
                "href": link.href
            }
        });
    })

/**
 * Renders the `App` component into the `root` node of the React application.
 */
root.render(<App />);

/**
 * `getRelName` is a function that extracts the name of a relation from a relation URL.
 * It does this by splitting the URL by '/' and returning the last element of the resulting array.
 *
 * @param {string} RelUrl - The relation URL.
 * @returns {string} - The name of the relation.
 */
function getRelName(RelUrl: string): string {
    return RelUrl.split("/").pop();
}