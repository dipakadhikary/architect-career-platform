import { useParams } from 'react-router-dom';
import { TutorialConceptPage } from './TutorialConceptPage';
import { TutorialQuestionsPage } from './TutorialQuestionsPage';
import { TutorialTopicPage } from './TutorialTopicPage';

/**
 * Dispatches /tutorials/* splat routes to concept, questions, or topic hub pages.
 */
export function TutorialSplatPage() {
  const { '*': splat = '' } = useParams();
  const normalized = splat.replace(/^\/+|\/+$/g, '');

  if (/\/concept$/i.test(normalized)) {
    return <TutorialConceptPage />;
  }
  if (/\/questions$/i.test(normalized)) {
    return <TutorialQuestionsPage />;
  }
  return <TutorialTopicPage />;
}
