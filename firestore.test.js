const { initializeTestEnvironment, assertFails, assertSucceeds } = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");
const net = require("node:net");

const PROJECT_ID = "demo-no-project";
const DATABASE_ID = "ai-studio-android-ecotrack-8b294afd-b381-4070-8dd6-edf1dd8a63f0";
const EMULATOR_HOST = "127.0.0.1";
const EMULATOR_PORT = 8085;
let testEnv;
let emulatorAvailable = false;

// The rules tests need the Firestore emulator. When it is not running
// (fresh clone, CI without emulators) skip instead of failing outright.
function isEmulatorRunning() {
  return new Promise((resolve) => {
    const socket = net.connect({ host: EMULATOR_HOST, port: EMULATOR_PORT, timeout: 1000 });
    socket.once("connect", () => { socket.destroy(); resolve(true); });
    socket.once("error", () => resolve(false));
    socket.once("timeout", () => { socket.destroy(); resolve(false); });
  });
}

before(async () => {
  emulatorAvailable = await isEmulatorRunning();
  if (!emulatorAvailable) {
    console.log(`Firestore emulator not reachable at ${EMULATOR_HOST}:${EMULATOR_PORT} — skipping rules tests. Start it with: firebase emulators:start --only firestore`);
    return;
  }
  testEnv = await initializeTestEnvironment({
    projectId: PROJECT_ID,
    firestore: {
      host: EMULATOR_HOST,
      port: EMULATOR_PORT,
      rules: fs.readFileSync("firestore.rules", "utf8")
    }
  });
});

after(async () => {
  if (testEnv) {
    await testEnv.cleanup();
  }
});

beforeEach(async () => {
  if (testEnv) {
    await testEnv.clearFirestore();
  }
});

test("Unauthenticated user cannot read or write user profiles", async (t) => {
  if (!emulatorAvailable) { t.skip("Firestore emulator not running"); return; }
  const unauthedDb = testEnv.unauthenticatedContext().firestore();
  const profileRef = unauthedDb.collection("users").doc("farmer1");

  await assertFails(profileRef.get());
  await assertFails(profileRef.set({
    userId: "farmer1",
    email: "farmer1@gmail.com",
    displayName: "Farmer One",
    createdAt: new Date(),
    updatedAt: new Date()
  }));
});

test("Authenticated farmer can create and read own profile", async (t) => {
  if (!emulatorAvailable) { t.skip("Firestore emulator not running"); return; }
  const authedDb = testEnv.authenticatedContext("farmer1").firestore();
  const profileRef = authedDb.collection("users").doc("farmer1");

  await assertSucceeds(profileRef.set({
    userId: "farmer1",
    email: "farmer1@gmail.com",
    displayName: "Md. Moniruzzaman",
    detectedAez: 1,
    detectedDistrict: "Dinajpur",
    createdAt: new Date(),
    updatedAt: new Date()
  }));

  await assertSucceeds(profileRef.get());
});

test("Farmer cannot access another farmer's profile or calculations", async (t) => {
  if (!emulatorAvailable) { t.skip("Firestore emulator not running"); return; }
  const farmer1Db = testEnv.authenticatedContext("farmer1").firestore();
  const farmer2Db = testEnv.authenticatedContext("farmer2").firestore();

  await assertSucceeds(farmer1Db.collection("users").doc("farmer1").set({
    userId: "farmer1",
    email: "farmer1@gmail.com",
    displayName: "Farmer 1",
    createdAt: new Date(),
    updatedAt: new Date()
  }));

  // Farmer 2 attempts to read Farmer 1's profile
  await assertFails(farmer2Db.collection("users").doc("farmer1").get());

  // Farmer 2 attempts to write a calculation under Farmer 1
  await assertFails(farmer2Db.collection("users").doc("farmer1").collection("calculations").doc("calc1").set({
    userId: "farmer2",
    cropName: "Boro Rice",
    cropBengaliName: "বোরো ধান",
    landArea: 33,
    landUnit: "DECIMAL",
    areaInHectares: 0.1335,
    soilFertilityClass: "LOW",
    totalCostTk: 3500,
    totalBags50kg: 2.5,
    createdAt: new Date(),
    updatedAt: new Date()
  }));
});

test("Farmer can create and read their own fertilizer calculations", async (t) => {
  if (!emulatorAvailable) { t.skip("Firestore emulator not running"); return; }
  const authedDb = testEnv.authenticatedContext("farmer1").firestore();
  const calcRef = authedDb.collection("users").doc("farmer1").collection("calculations").doc("calc1");

  await assertSucceeds(calcRef.set({
    userId: "farmer1",
    cropName: "Boro Rice",
    cropBengaliName: "বোরো ধান",
    landArea: 33,
    landUnit: "DECIMAL",
    areaInHectares: 0.1335,
    soilFertilityClass: "LOW",
    cowdungTonnesPerHa: 5,
    totalCostTk: 3500,
    totalBags50kg: 2.5,
    aezNumber: 1,
    aezName: "Old Himalayan Piedmont Plain",
    createdAt: new Date(),
    updatedAt: new Date()
  }));

  await assertSucceeds(calcRef.get());
});
