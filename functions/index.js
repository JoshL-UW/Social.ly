/**
 * Import function triggers from their respective submodules:
 *
 * const {onCall} = require("firebase-functions/v2/https");
 * const {onDocumentWritten} = require("firebase-functions/v2/firestore");
 *
 * See a full list of supported triggers at https://firebase.google.com/docs/functions
 */
const {onSchedule} = require("firebase-functions/v2/scheduler");
// const functions = require("firebase-functions/v2/firestore");
const admin = require("firebase-admin");
admin.initializeApp();

// Create and deploy your first functions
// https://firebase.google.com/docs/functions/get-started

exports.pairParticipants = onSchedule("every 1 minutes", async (context) => {
  const db = admin.firestore();
  // TODO: EVENT ID how to transfer here

  const eventCodes = await db.collection("EventCodes").get();

  if (eventCodes.empty) {
    console.log("Event does not exist.");
    return null;
  }

  for (const eventDoc of eventCodes.docs) {
    const eventId = eventDoc.id;
    const eventData = eventDoc.data();

    const people = (eventData.participants || [])
        .filter((person) => person !== null);
    // TODO: handle waiting for more people
    const result = generatePairs(people);

    const formattedPairs = [];

    for (let i = 0; i < result.pairs.length; i += 2) {
      if (result.pairs[i] !== null && result.pairs[i + 1] !== null) {
        formattedPairs.push({
          participant1: result.pairs[i],
          participant2: result.pairs[i + 1],
        });
      }
    }
    //  const pairs = result.pairs;
    //  const newOrder = result.newOrder;


    const eventRef = db.collection("EventCodes").doc(eventId);

    await new Promise((resolve) => setTimeout(resolve, 2000));
    await eventRef.update({
      pairs: formattedPairs,
      participants: result.newOrder.filter((person) => person !== null),
      lastUpdated: admin.firestore.FieldValue.serverTimestamp(),
      nextUpdateTime: admin.firestore.Timestamp
          .fromDate(new Date(Date.now() + 60 * 1000)),
    });
  }
  return null;
});
/**
 * Generates unique pairs of participants. This method also adds a 'dummy' item
 * to the end of the passed list if it contains an odd number of people.
 *
 * @param {Array} newOrder - An array of all participants.
 * @return {Object} - An object containing:
 *  1.`pairs`: An array of generated pairs where elements 0 and 1 form a pair,
 *       2 and 3 form a pair, and so on.
 *  2.`newOrder`: An array representing the new rotated order
 *       for the next round.
 */
function generatePairs(newOrder) {
  // check for even number of people
  if (newOrder.length % 2 !== 0) {
    newOrder.push(null); // add a dummy person to make even
  }

  const pairings = []; // used to return the created pairs

  let firstPtr = 0; // points to the first element in the pair
  let lastPtr = newOrder.length - 1; // points to the last element in the pair

  for (let i = 0; i < newOrder.length / 2; i++) {
    pairings.push(newOrder[firstPtr]);
    pairings.push(newOrder[lastPtr]);
    firstPtr += 1;
    lastPtr -= 1;
  }

  newOrder = rotateList(newOrder); // rotate the list for the next round

  return {pairs: pairings, newOrder};
}

/**
* A helper method used by generatePairs(). Rotates all of the items in a
* list except for the first one.
*
* @param {Array} toRotate -- the ArrayList of people to be rotated
* @return {Array} toRotate -- the same ArrayList after it has been rotated
*/
function rotateList(toRotate) {
  const rotated = [...toRotate];
  const temp = rotated[1];
  rotated.splice(1, 1);
  rotated.push(temp);
  return rotated;
}
