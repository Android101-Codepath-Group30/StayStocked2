Stay Stocked

## Table of Contents

1. [App Overview](#App-Overview)
1. [Product Spec](#Product-Spec)
1. [Wireframes](#Wireframes)
1. [Build Notes](#Build-Notes)

## App Overview

### Description 

The Stay stocked app is a user-friendly mobile application designed to help individuals keep track of their groceries. It sends reminders when items are about to expire, and notifies users of the estimated amount of money lost when food items expire. By facilitating better management of food waste and encouraging budget consciousness, this app is a valuable tool for users.

### App Evaluation

 -  **Mobile:** Both pc and mobile but preference on mobile
    - Has the potential to be a highly unique mobile experience.
    - For example: Users could use their device camera to scan barcodes of grocery items to quickly add them to their shopping list.
    - The app could also use location services to suggest nearby grocery stores and send reminders to users when they are close to a store where they need to purchase items. 
-   **Story:**
    - Grocery tracker that lets you know when your food will expired and how much money your losing
    - This helps people comprehend the amount of money lost and decided on better grocery shopping habits
    - We think our friend will enjoy this product if they make it a habit to input food and check expiration dates 
-   **Market:**
    - The market for this app could be quite large, as it could be useful to a variety of different groups of people, such as home owners, college students, apartment dwellers, etc.
    - The potential user base for a grocery tracker app is quite large and diverse, as anyone who buys and consumes groceries can benefit from it.
    - It can also be useful for people with dietary restrictions or preferences, as they can easily keep track of items that fit their needs.
    - The app provides significant value to people who lead busy lives or have difficulty keeping track of what they need to buy or what they have in their
            pantry. It can also be valuable to those who want to save MONEY by avoiding waste or by taking advantage of sales.
-   **Habit:**   
    - It can be if UI is seamless and easy to use otherwise people wont use it
    - If the app is designed to be simple, intuitive, and user-friendly, it could become habit-forming for users who rely on it for their grocery tracking needs. If the app offers useful features like reminders and easy ways to add items to the list, users may find themselves opening and using it frequently because it covers a need.
    - For some users, they may only need to use the app once a week or so, while others may use it daily or multiple times a day. It's important to design the app in a way that makes it easy to use and incorporate into a user's routine.
    - The app allows users to create and maintain a grocery list, so it is both a consumption and creation tool. Users consume the app by using it to view their grocery list, but they also create something by adding items to the list and organizing their groceries.
-   **Scope:** 
    - The technical challenge of completing the app by the deadline would depend on the skills and experience of the development team. However, integrating with the Kroger API and implementing database storage could pose some challenges.
    - Need database to save list
    - Or just to save to array /saved locally
    - Search for food
    - A stripped-down version of this app could still be interesting to build, with the focus on the essential features such as search and list creation. This could also make the app more accessible to a wider user base.
    - Its clearly define with both required and stretch features specified and has the potential to provide value to users.

## Product Spec

### 1. User Features (Required and Optional)

Required Features:

- [x] Users can search for food of choice e.g bread, salt
- [x] User can view food item information  
- [x] Users can save item to a list
- [x] User can view items save to list

Stretch Features:

- Users can save item to a database (Room DB)
- Users can adjust list and food information
- User receive a reminder when food item is about to expire

### 2. Chosen API(s)

- **Kroger** 
  - https://developer.kroger.com/reference/#section/Introduction
  - Users can search for food of choice e.g bread, salt
  - User can filter by item 

### 3. User Interaction

Required Feature

- **Kroger**
    - [X] **Users can search for food of choice**
      - => User type a search term ( for example bread, beer, etc)
      - => Results display based on the recycler view
    - [X] **Users can add items to list**
      - => User can find food item 
      - => Users click on a button to add item to My List
      - => Toast displays select item is added to list
    - [X] **User can view food items on list**
      - => User click MyList buttons
      - => Toast items all items saved on list

## Wireframes

### Low-Fidelity Wireframe - Sketch
<img src="https://github.com/Android101-Codepath-Group30/StayStocked/blob/main/wireframes/low-fidelity.jpg" width=300 height=400>

### [BONUS] Digital Wireframes & Mockups
<img src="https://github.com/Android101-Codepath-Group30/StayStocked/blob/main/wireframes/wireframe_v1.jpg" width=600>

### [BONUS] Interactive Prototype

## Build Notes

The most significant hurdle we faced while constructing our project was integrating everyone's code and managing merge conflicts. Additionally, we had to alter our initial concept due to inconsistencies between the information obtained from an API call and the information documented in the API. To illustrate, the API documentation listed "price" as a field present in the JSON object, but it was absent when we queried the json object from the product search component in the Kroger API.

For Milestone 2, include **2+ GIFs** of the build process here!
<table>
  <tr>
   <th>Iteration 1</th>
   <th>Iteration 2</th>
   <th>Iteration 3</th> 
  </tr>
  <tr>
  <td><img src="https://github.com/Android101-Codepath-Group30/StayStocked2/blob/main/demoAssets/UI_stay_stock.png" width=300 height=300></td>
  <td><img src="https://github.com/Android101-Codepath-Group30/StayStocked2/blob/main/demoAssets/search-toast.gif" width=300 height=500></td>
 <td><img src="https://github.com/Android101-Codepath-Group30/StayStocked2/blob/main/demoAssets/StayStock2_thirdrun.gif" width=300 height=500></td>
 </tr>
</table>

### Final Iteration

<img src="https://github.com/Android101-Codepath-Group30/StayStocked2/blob/main/demoAssets/final-demo.gif" width=300 height=550>

## License

Copyright **2023** **Gabriela Liera, Shi Zhang, Kaosisochukwu Nwosu,Lyton Mhlanga, Amarilys Otero Ayala**

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
