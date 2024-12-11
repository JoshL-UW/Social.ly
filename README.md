# Social.ly

For demonstration purposes, use
  Username: demonstrationUser
  Password: password
  Event Code: 7ot3KAb8I0

---TEAM CONTRIBUTIONS---
Josh:

Gunnar: The majority of my contributions focused on the backend, with additional work on the frontend UI to support the backend functionality. Frontend work included designing a few of the initial screens, and adding necessary elements/navigation for the backend to work. Set up Firebase authentication, and along with Josh, implemented the signin/account creation functionalities. Developed the profile creation/edit features with the use of Firebase/Firestore, which uses the auth token of the currently signed in user to retrieve their information from a collection in the database. Also had to implement significant changes to use a profile picture as Firebase requires a paid subscription to store photos. This involed encoding the uploaded photos to Base64 and then storing the string in Firestore, then retrieving and decoding the Base64 to an image where necessary. Implemented the frontend and backend of the Saved Connections screen to display a list of the profiles that the user has saved. Used the pairing algorithm written by Max and Gabriel to implement the pairing and main event logic. This was achieved by deploying a cloud function to Firebase which essentially took an Array of the users in each event and passed them to Max and Gabriels pairing logic to return pairs, which are changed every 5 minutes. Then in the fragment that displays the current match, the current partner can be retrieved from Firestore and their information shown on the screen. Created event instance for demonstration purposes. NOTE: github contributions is greatly inflated (by ~7000) due to committing the Firebase functions folder.

Max and Gabriel: Worked on all code pushed by either Max or Gabriel together. Designed some of the initial UI/screens. Reformatted original purple screens to have orange gradient, be more consistent, and have a more professional look. Designed backend algorithm to randomly pair each participant with another random participant every round of the event; algorithm designed to prevent repeat pairings across rounds and eventually produce all possible pairs. Implemented NFC to save connections while paired during an event. Also implemented button alternative to NFC for non NFC-compatible devices (next to chat button). Designed and implemented splash screen when app is opened and "how to use Social.ly" screen. NOTE: we have very few commits to the main branch of the repository because Android Studio runs very slowly (if at all) on our laptops; groupmates ensured our code functioned properly and push to main instead. Most of our commits are to other branches.
