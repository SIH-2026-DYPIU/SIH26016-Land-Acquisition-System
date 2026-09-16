# NLAMS — SIH 2026

National Land Acquisition & Management System frontend + backend integration workspace.

## Open in VS Code

Open the `NLAMS-SIH-2026` folder in VS Code.

## Frontend

```text
frontend/
  index.html
  package.json
  vite.config.js
  .env.example
  src/
    App.jsx
    main.jsx
    index.css
    auth/
      LoginPage.jsx
      auth.css
    services/
      api.js
```

Run:

```bash
cd frontend
npm install
npm run dev
```

The Vite development server proxies `/api/*` requests to `http://localhost:8080`.

The login screen is explicitly in SIH prototype/demo mode by default. It is not government identity verification.

## Backend

The `backend/` folder contains the backend integration files that were available from the previous integration work:

- authentication controller/service
- dashboard controller/service
- project controller
- land parcel controller
- acquisition stage controller
- compensation controller
- affected family controller
- CORS configuration
- JWT property snippet

### Important

This archive does **not** claim to contain the entire original Spring Boot backend project. The complete original backend root (including the user's existing `pom.xml`, application class, entities/models, DTOs, repositories, and all seven original service implementations) was not available as a complete uploaded project in this conversation.

Those files must remain part of the existing backend project. The files in this archive are intended to be merged into that backend package structure.

## Architecture notes

The SIH blueprint uses five authenticated internal roles:

1. System Administrator
2. Government Officer
3. Project Agency
4. Acquisition Officer
5. Field Officer

Public users have public-only access.

Authorization should follow:

`User -> Role -> Scope -> Permissions -> Dashboard`

Backend authorization should consider role permission, scope, and workflow state.

## Framework notes

The frontend is JavaScript/JSX, intentionally kept as `.jsx` rather than converting the existing application to TypeScript.


## GIS Map

The GIS page now uses Leaflet with OpenStreetMap tiles instead of the previous placeholder map. It includes a national India view, project markers, parcel markers, search/focus, layer toggles, click-to-inspect popups, and export of the prototype GIS data.

Install frontend dependencies with `npm install`, then run `npm run dev`.

The current map coordinates are **prototype/demo data**. For the SIH production architecture, replace them with approved GeoJSON/WMS/WMTS layers and parcel geometries served from the backend/PostGIS.
