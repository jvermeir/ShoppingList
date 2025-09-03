# WARP.md

This file provides guidance to WARP (warp.dev) when working with code in this repository.

## Project Overview

This is a multi-generational shopping list application that has evolved from Oracle Forms → Java → Scala → current Kotlin/TypeScript implementation. The current version uses an NX monorepo structure with a React frontend and Spring Boot API backend.

**Key Purpose**: Generate optimized shopping lists based on recipes and menus, organized by store layout categories.

## Architecture

### Monorepo Structure
- **Root**: Contains legacy Scala version (production branch) and shell scripts
- **shop/**: NX workspace containing the modern implementation
  - **apps/shop**: React/TypeScript frontend using Material-UI
  - **apps/shop-api**: Spring Boot API in Kotlin with H2 database
  - **libs/service**: Shared TypeScript interfaces and API client functions

### Domain Model
The system revolves around these core entities with hierarchical relationships:
- **Categories** → **Ingredients** → **RecipeIngredients** → **Recipes**
- **Menus** → **MenuItems** (recipes for specific days)
- **ShoppingLists** generated from menus, organized by store categories

### Data Flow
1. Define categories with shop ordering (produce, dairy, etc.)
2. Create ingredients assigned to categories
3. Build recipes from ingredients with quantities
4. Create weekly menus by assigning recipes to days
5. Generate shopping lists from menus, automatically organized by store layout

## Common Development Commands

### Development Servers
```bash
# Start both frontend and backend (run in separate terminals from shop/ directory)
nx serve shop          # Frontend at http://localhost:4200 with proxy to backend
nx serve shop-api      # Backend API at http://localhost:8080

# Or use root-level convenience scripts
./bin/runShop.sh       # Legacy Scala version
```

### Build Commands
```bash
# From shop/ directory
nx build shop          # Frontend production build
nx build shop-api      # Backend JAR build

# Backend-specific Gradle commands  
cd apps/shop-api
./gradlew build        # Build with tests
./gradlew bootRun      # Run Spring Boot app
```

### Testing
```bash
# Frontend tests
nx test shop

# Backend tests
nx test shop-api
# or
cd apps/shop-api && ./gradlew test

# Run specific test
cd apps/shop-api && ./gradlew test --tests "*CategoryTest*"
```

### Linting and Formatting
```bash
nx lint shop           # Frontend linting
nx format shop-api     # Backend Kotlin formatting (using nx-spring-boot plugin)
```

### Database Management
- H2 in-memory database auto-creates tables from `schema.sql`
- Use `/cleanup` endpoint to reset all data during development
- Database console available at http://localhost:8080/h2-console (when DevTools active)

## Code Organization Patterns

### Backend (Kotlin/Spring Boot)
- **Domain-per-file**: Each entity (Category, Recipe, etc.) contains Entity, Repository, Service, and RestController in one file
- **Services handle business logic**: Validation, data transformation, cross-entity operations
- **Resources are thin**: Single-line methods delegating to services
- **Custom queries**: Use `@Query` for complex database operations like `ingredientsView()`

### Frontend (React/TypeScript)
- **Page components** in `apps/shop/src/app/pages/` handle routing and top-level state
- **Feature components** in `apps/shop/src/app/components/[domain]/` handle specific functionality
- **Shared service layer** in `libs/service/` provides typed API calls and interfaces
- **Material-UI** components with responsive breakpoints for mobile/desktop

### API Patterns
- RESTful endpoints following pattern: `/api/[entity]` and `/api/[entities]`
- Date handling: Use LocalDate with ISO format (`YYYY-MM-DD`)
- UUIDs for all primary keys
- Shopping list operations use path parameters for IDs and amounts

## Key Implementation Details

### Date Handling
- Backend: `LocalDate` (java.time) with custom `StringToDateConverter`
- Frontend: ISO date strings, Date objects for date pickers
- API: ISO date format in path parameters and JSON

### Testing Strategy
- Backend: Integration tests using SpringBootTest with H2, real repositories
- Tests use `@Transactional` for automatic rollback
- Custom fixtures and test data builders in `Fixtures.kt`
- Frontend: Jest with React Testing Library

### Error Handling
- Custom `ResourceNotFoundException` with global `@ControllerAdvice` handler
- Frontend error states handled in service layer with callback patterns

### Legacy Integration
- Shell scripts in `bin/` for Scala version (production)
- Data files expected in `../ShoppingListData/` directory
- Gradle wrapper included for backend builds

## Development Notes

### Responsive Design
- Uses Material-UI breakpoints (xs, sm, md, lg, xl)
- Custom `StyledTableCell` components for mobile-friendly tables
- Breakpoint-specific padding and sizing

### State Management
- React components use local state and prop drilling
- Service layer abstracts API calls with loading/error states
- No global state management (Redux/Context) currently implemented

### Hot Module Replacement
- Frontend supports HMR via Webpack dev server
- Backend supports DevTools for automatic restart on changes
