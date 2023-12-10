import * as React from 'react';
import {createRoot} from 'react-dom/client';
import {App} from './App';
// import {getHome} from "./Service/GomokuService";

const root = createRoot(document.getElementById('main-div'));
// export const linkRecipe: Promise<void> = getHome()
//         .then((data) => {
//             data.recipeLinks.map((link) => {
//                 return {
//                     "rel": getRelName(link.rel[0]),
//                     "href": link.href
//                 }
//             });
//         })

root.render(<App />);

// function getRelName(RelUrl: string): string {
//     return RelUrl.split("/").pop();
// }