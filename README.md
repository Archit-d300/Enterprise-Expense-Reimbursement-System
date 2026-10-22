# Enterprise Expense Reimbursement System

Java console application demonstrating claim creation, approval, reimbursement,
persistence, notifications, and reporting patterns.

## End-to-end flow

New claims can be constructed step by step with `ExpenseClaimBuilder` or copied
from a previous claim using `ExpenseClaim.cloneExpense()`. Both paths create
independent drafts and enter the same request validation, role-policy, receipt
validation, approval, payment, persistence, and notification flow. Receipt
references are required before a claim can be approved.

`ApprovalMediator` coordinates receipt validation and, where the role policy
requires it, the manager, department, finance, and final approval stages.
Payment is only attempted for approved claims. SQLite stores pending and
processed claims, including department and receipt-reference details.

The Template Method reports read processed rows without changing them:

- Employee reports expose only the requester's claims.
- Department reports require a manager, administrator, or executive and a
  department filter.
- Finance reports require a finance officer, administrator, or executive and
  summarize reimbursement and payment-failure outcomes.

Other examples include Abstract Factory and Factory for policies and expense
types, Singleton for the shared registry, Observer for claim status changes,
Proxy for request and payment checks, and notifications for processing outcomes.

## Build and run (PowerShell)

Run these commands from the project folder:

```powershell
$sources = Get-ChildItem src,test -Recurse -Filter *.java | ForEach-Object { $_.FullName }
javac -cp 'lib\*' -d build $sources
java --enable-native-access=ALL-UNNAMED -cp 'build;lib\*' PatternIntegrationTest
```

## Run the walkthrough demo

From the project folder, run:

```powershell
.\run.ps1
```

The script compiles the source and test files, then runs the demo. Alternatively,
run the commands below manually:

```powershell
$sources = Get-ChildItem src,test -Recurse -Filter *.java | ForEach-Object { $_.FullName }
javac -cp 'lib\*' -d build $sources
java --enable-native-access=ALL-UNNAMED -cp 'build;lib\*' Main --demo
```

The demo is available through the same `Main` entry point. It uses an
in-memory SQLite database and console-only notifications; it does not send
email or modify `expense_system.db`. It walks through Builder and Prototype
claim creation, expense and user factories, role-based abstract factories,
the singleton registry, approval mediator and chain, observer notifications,
request and payment proxies, policy and receipt rejection, and a guarded
payment failure. It persists example claims in memory and finishes with
employee, department, and finance reports using the Template Method.

## Run the seeded database application

```powershell
java --enable-native-access=ALL-UNNAMED -cp 'build;lib\*' Main
```

`Main` creates or reuses `expense_system.db` and processes pending sample rows.
Use `java --enable-native-access=ALL-UNNAMED -cp 'build;lib\*' Main --reset`
to reset that database and seed it again. The native-access flag suppresses the
SQLite JDBC warning on recent JDKs. Notifications use the console by default.
To opt into Gmail SMTP, set `EXPENSE_SMTP_ENABLED=true`,
`EXPENSE_SMTP_EMAIL`, and `EXPENSE_SMTP_APP_PASSWORD` in the environment before
launching `Main`. If sending fails, the application reports the error and
prints the notification to the console.