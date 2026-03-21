const { chromium } = require('playwright');
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

  const browser = await chromium.launch({
    headless: true,
    args: ['--no-sandbox', '--disable-setuid-sandbox']
  });

  const context = await browser.newContext({
    viewport: { width: 1400, height: 900 }
  });

  const results = [];

  for (const pageConfig of PAGES) {
    const page = await context.newPage();

    // Capture console messages
    page.on('console', msg => {
      console.log(`  [Console] ${msg.type()}: ${msg.text()}`);
    });
    page.on('pageerror', error => {
      console.log(`  [Page Error] ${error.message}`);
    });

    try {
      console.log(`Capturing: ${pageConfig.url}`);
      await page.goto(`${BASE_URL}${pageConfig.url}`, {
        waitUntil: 'networkidle',
        timeout: 30000
      });

      // Wait for Vue to render - wait for main content element
      try {
        await page.waitForSelector(pageConfig.waitFor, { timeout: 10000 });
      } catch (e) {
        console.log(`  Warning: selector "${pageConfig.waitFor}" not found, using timeout`);
      }

      // Additional wait for animations/data
      await page.waitForTimeout(3000);

      const screenshotPath = path.join(SCREENSHOTS_DIR, `${pageConfig.name}.png`);
      await page.screenshot({
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
    } catch (error) {
      results.push({
        page: pageConfig.title,
        url: pageConfig.url,
        status: 'error',
        error: error.message
      });
      console.error(`  Error: ${error.message}`);
    } finally {
      await page.close();
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