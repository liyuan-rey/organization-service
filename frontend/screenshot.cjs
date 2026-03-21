const puppeteer = require('puppeteer');
const path = require('path');
const fs = require('fs');

const BASE_URL = 'http://localhost:3000';
const SCREENSHOTS_DIR = path.join(__dirname, 'screenshots');

// Pages to capture
const PAGES = [
  { url: '/', name: 'dashboard', title: 'Dashboard', waitFor: 'main' },
  { url: '/departments', name: 'departments', title: 'Departments', waitFor: 'table' },
  { url: '/personnel', name: 'personnel', title: 'Personnel', waitFor: 'table' },
  { url: '/positions', name: 'positions', title: 'Positions', waitFor: 'table' },
  { url: '/department-positions', name: 'department-positions', title: 'DepartmentPositions', waitFor: 'table' },
  { url: '/personnel-positions', name: 'personnel-positions', title: 'PersonnelPositions', waitFor: 'table' },
];

async function takeScreenshots() {
  // Create screenshots directory
  if (!fs.existsSync(SCREENSHOTS_DIR)) {
    fs.mkdirSync(SCREENSHOTS_DIR, { recursive: true });
  }

  const browser = await puppeteer.launch({
    headless: 'new',
    args: ['--no-sandbox', '--disable-setuid-sandbox']
  });

  const results = [];

  for (const pageConfig of PAGES) {
    try {
      const tab = await browser.newPage();
      await tab.setViewport({ width: 1400, height: 900 });

      // Capture console messages
      tab.on('console', msg => {
        console.log(`  [Console] ${msg.type()}: ${msg.text()}`);
      });
      tab.on('pageerror', error => {
        console.log(`  [Page Error] ${error.message}`);
      });

      console.log(`Capturing: ${pageConfig.url}`);
      await tab.goto(`${BASE_URL}${pageConfig.url}`, {
        waitUntil: 'networkidle2',
        timeout: 30000
      });

      // Wait for Vue to render - wait for main content element
      try {
        await tab.waitForSelector(pageConfig.waitFor, { timeout: 10000 });
      } catch (e) {
        console.log(`  Warning: selector "${pageConfig.waitFor}" not found, using timeout`);
      }

      // Additional wait for animations/data
      await new Promise(resolve => setTimeout(resolve, 3000));

      const screenshotPath = path.join(SCREENSHOTS_DIR, `${pageConfig.name}.png`);
      await tab.screenshot({
        path: screenshotPath,
        fullPage: false
      });

      results.push({
        page: pageConfig.title,
        url: pageConfig.url,
        screenshot: screenshotPath,
        status: 'success'
      });

      console.log(`  Saved: ${screenshotPath}`);
      await tab.close();
    } catch (error) {
      results.push({
        page: pageConfig.title,
        url: pageConfig.url,
        status: 'error',
        error: error.message
      });
      console.error(`  Error: ${error.message}`);
    }
  }

  await browser.close();
  return results;
}

takeScreenshots()
  .then(results => {
    console.log('\n=== Screenshot Results ===');
    results.forEach(r => {
      if (r.status === 'success') {
        console.log(`[OK] ${r.page}: ${r.screenshot}`);
      } else {
        console.log(`[FAIL] ${r.page}: ${r.error}`);
      }
    });
  })
  .catch(console.error);