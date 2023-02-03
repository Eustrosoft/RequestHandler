// Core libraries such as react, angular, redux, ngrx, etc. must be
// singletons. Otherwise, the applications will not work together.
const coreLibraries = new Set([
  '@angular/animations',
  '@angular/cdk',
  '@angular/common',
  '@angular/compiler',
  '@angular/core',
  '@angular/forms',
  '@angular/material',
  '@angular/platform-browser',
  '@angular/platform-browser-dynamic',
  '@angular/router',
  'rxjs',
  'tslib',
  'zone.js',
]);

module.exports = {
  // Share core libraries, and avoid everything else
  shared: (libraryName, defaultConfig) => {
    if (coreLibraries.has(libraryName)) {
      return defaultConfig;
    }

    // Returning false means the library is not shared.
    return false;
  },
};
