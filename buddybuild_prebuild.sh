# Jest won't create our folder structure for us, so lets create it first
mkdir buddybuild_artifacts

mkdir buddybuild_artifacts/Jest

#jest --outputFile=buddybuild_artifacts/Jest/jest.json --json
# Some users might want to just run
npm run test --coverage --outputFile=buddybuild_artifacts/Jest/jest.json --json
#instead, depending on how they have everything set up