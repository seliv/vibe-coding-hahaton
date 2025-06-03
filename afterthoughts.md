## My Brief Takeaway:
    
- After ~10 hours with Junie I have nothing resembling a working PoC.
    The result is more like an early stage skeleton that barely deploys and runs, 
    speaking not of even basic functionality, tests, production deployment, performance analysis.
- I've invested significant time into initial requirements and documentation 
    to try "read the requirements.md and implement it" approach advertised by JetBrains, 
    this failed miserably. Junie ignores guidelines and skips tasks while claims "everything is implemented".
- I made no manual adjustments, not even the smallest ones, everything was purely vibe coding; 
    the only files I wrote manually were .md docs.
- Keeping the solution runnable both integrated in Docker and from IDE with debugging took enormous effort;
    initial Docker configuration wasn't quick either.
- Even after establishing a running backend+frontend bundle, Junie kept breaking things here and there
    on each update and often was unable to fix them properly — I had to switch to Cursor.

I see the others have significantly more impressive progress.
Apparently, there were three main reasons for me being way behind. I can't say for sure which of them contributes the most:
- Lack of prompt engineering skill — I am just a casual user, not a pro.
- Inferior choice of the tool/model.
- Using entry-level subscription plans.

## The Skills

Regarding the skills that were useful, I'd say what humans have and the AI lacks are:
- Ability to view and analyze at all levels — from high-level design down to micro decisions.
- Knowing what to check and where to poke with a stick so the things break.
- Attention to details (that's counterintuitive, I expected that machine should be better than human at it).
- Regression, checking that things previously done are not broken with the next step (counterintuitive again).

## AI Interaction Experience
Retrospectively, the **AI behaved very humanish**: it was over-self-confident, forgetful, sleazy, and careless:
- It disobeys or clearly ignores instructions and does the things its own way.
- It misses the requests and can't focus on details.
- It cheats on reporting progress:
    claims everything works correctly (while it doesn't) and all aspects are implemented (while they are not).
- It then obediently reiterates when the flaws are pointed out but not always to success.

Ironically, this is something I'd reasonably expect from a human actor, especially an entry-level engineer, 
but it's counterintuitive to see this kind of behavior from a machine.

Maybe what we need in terms of skills is **soft skills** and **human management**, not technical excellence.

## Some Random Observations
- Initial codebase generation took around 20 minutes.
- It took around one more hour to make it locally buildable and runnable.
- One more hour to establish basic client-server interaction with the user able to log in.
- Around one hour trying to set up Docker deployment, Junie failed at it, I switched to Cursor.
- One more hour with Cursor to make the application run correctly in local Docker.
- Two hours trying to implement functional requirements, the application never reached any stable version.

## Impression About Junie
- Neither Junie nor Cursor were able to solver Angular-Nginx-Java communication pipeline.
    They ended up with duplicated `/api/api/` in URLs resolved at Nginx level - a messy hack.
- When pointed out that `/users/search` endpoint returns `HTTP 403`, Junie added this endpoint to `permitAll()` set.  
- Junie often reported things like "the backend and frontend were modified and all tests run correctly"
    while there were no changes in frontend files and tests didn't exist at all.
- Junie often walked in circles trying to switch between two wrong solutions back and forth.
- Junie seemed less capable of doing things on its own for debugging (e.g. run Docker or curl).