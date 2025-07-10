# Period-Based Analytics

This document describes the new period-based analytics feature that allows filtering session analytics by time periods.

## Overview

The analytics endpoints now support an optional `period` query parameter that allows you to filter sessions based on different time periods:
- `week`: Current ISO week
- `month`: Current month
- `year`: Current year
- If not specified or invalid: All time (default behavior)

## Supported Endpoints

All analytics endpoints now support the `period` parameter:

### 1. Session Analytics
```
GET /api/sessions/analytics?period=week
GET /api/sessions/analytics?period=month
GET /api/sessions/analytics?period=year
```

### 2. Stats Overview
```
GET /api/sessions/stats/overview?period=week
GET /api/sessions/stats/overview?period=month
GET /api/sessions/stats/overview?period=year
```

### 3. Progress Analytics
```
GET /api/sessions/stats/progress?period=week
GET /api/sessions/stats/progress?period=month
GET /api/sessions/stats/progress?period=year
```

### 4. Highest Grades
```
GET /api/sessions/stats/highest?period=week
GET /api/sessions/stats/highest?period=month
GET /api/sessions/stats/highest?period=year
```

### 5. Average Grades
```
GET /api/sessions/stats/average?period=week
GET /api/sessions/stats/average?period=month
GET /api/sessions/stats/average?period=year
```

## Period Definitions

### Week
- Uses ISO week numbering (ISO-8601)
- Includes all sessions from the current ISO week
- Example: If today is Wednesday of week 25, it includes all sessions from Monday to Sunday of week 25

### Month
- Includes all sessions from the current calendar month
- Example: If today is July 15th, it includes all sessions from July 1st to July 31st

### Year
- Includes all sessions from the current calendar year
- Example: If today is July 15th, 2025, it includes all sessions from January 1st to December 31st, 2025

### All Time (Default)
- When no period is specified or an invalid period is provided
- Includes all sessions regardless of date

## Example Usage

### Request
```bash
curl -X GET "http://localhost:8080/api/sessions/analytics?period=month" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Response
```json
{
  "totalSessions": 5,
  "averageDifficulty": 7.5,
  "sentPercentage": 60.0,
  "sessionsByDiscipline": {
    "BOULDER": 3,
    "LEAD": 2
  },
  "averageDifficultyByDiscipline": {
    "BOULDER": 6.0,
    "LEAD": 9.0
  },
  "sentPercentageByDiscipline": {
    "BOULDER": 66.7,
    "LEAD": 50.0
  }
}
```

## Implementation Details

### Controller Changes
- Added optional `@RequestParam(required = false) String period` to all analytics endpoints
- Period parameter is passed to the service layer for filtering

### Service Layer Changes
- New `getSessionsByPeriod()` method that filters sessions based on the period
- Helper methods for date filtering:
  - `isInCurrentWeek()`: Uses ISO week fields for accurate week calculation
  - `isInCurrentMonth()`: Uses YearMonth for month comparison
  - `isInCurrentYear()`: Simple year comparison
- Graceful handling of invalid periods (returns all sessions)

### Error Handling
- Invalid period values are treated as "all time"
- Null or empty period values default to "all time"
- No breaking changes to existing API behavior

## Testing

The feature includes comprehensive tests:
- Unit tests for period filtering logic
- Integration tests for all endpoints with period parameters
- Edge case testing (invalid periods, null values, etc.)

## Frontend Integration

The frontend can now pass the period parameter to get filtered analytics:

```javascript
// Example: Get this month's analytics
const response = await fetch('/api/sessions/analytics?period=month', {
  headers: {
    'Authorization': `Bearer ${token}`
  }
});

const analytics = await response.json();
```

## Backward Compatibility

This feature is fully backward compatible:
- Existing API calls without the period parameter continue to work
- All existing functionality remains unchanged
- New period parameter is optional and defaults to "all time" 