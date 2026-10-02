import { Children, isValidElement, useEffect, useId, useState, type ReactNode } from 'react';
import { Box, Typography } from '@mui/material';
import ReactMarkdown from 'react-markdown';
import remarkGfm from 'remark-gfm';
import rehypeHighlight from 'rehype-highlight';
import rehypeSanitize, { defaultSchema } from 'rehype-sanitize';
import type { Components } from 'react-markdown';
import 'highlight.js/styles/github.css';

interface MarkdownViewerProps {
  content: string;
  /** Enables GFM + syntax highlighting for fenced code blocks. */
  withSyntaxHighlight?: boolean;
}

const sanitizeSchema = {
  ...defaultSchema,
  attributes: {
    ...defaultSchema.attributes,
    code: [...(defaultSchema.attributes?.code ?? []), ['className']],
    span: [...(defaultSchema.attributes?.span ?? []), ['className']],
  },
};

const markdownComponents: Components = {
  a: ({ href, children, ...props }) => (
    <a href={href} target="_blank" rel="noopener noreferrer" {...props}>
      {children}
    </a>
  ),
  pre: ({ children }) => <MarkdownPre>{children}</MarkdownPre>,
};

/** Light grey fence — readable ASCII diagrams and source on the tutorial page. */
const CODE_FENCE_BG = '#e8eaed';
const CODE_FENCE_FG = '#1f2937';
const INLINE_CODE_BG = 'rgba(15, 23, 42, 0.06)';

const MERMAID_START =
  /^(?:flowchart|graph|sequenceDiagram|classDiagram|stateDiagram(?:-v2)?|erDiagram|gantt|pie|journey|gitGraph|mindmap|timeline|quadrantChart|requirementDiagram|C4Context|block-beta|architecture-beta|sankey-beta|xychart-beta)\b/;

let mermaidLoader: Promise<typeof import('mermaid').default> | null = null;

function loadMermaid() {
  if (!mermaidLoader) {
    mermaidLoader = import('mermaid').then((mod) => {
      const mermaid = mod.default;
      mermaid.initialize({
        startOnLoad: false,
        securityLevel: 'antiscript',
        theme: 'neutral',
        fontFamily: '"IBM Plex Sans", "Segoe UI", sans-serif',
        flowchart: { htmlLabels: true, curve: 'basis' },
      });
      return mermaid;
    });
  }
  return mermaidLoader;
}

function nodeToText(node: ReactNode): string {
  if (typeof node === 'string' || typeof node === 'number') return String(node);
  if (Array.isArray(node)) return node.map(nodeToText).join('');
  if (isValidElement<{ children?: ReactNode }>(node)) {
    return nodeToText(node.props.children);
  }
  return '';
}

function mermaidSourceFromPre(children: ReactNode): string | null {
  for (const child of Children.toArray(children)) {
    if (!isValidElement<{ className?: string; children?: ReactNode }>(child)) continue;
    const className = child.props.className ?? '';
    const source = nodeToText(child.props.children).replace(/\n$/, '').trim();
    if (!source) continue;
    const language = /language-([\w-]+)/.exec(className)?.[1];
    const firstLine = source.split('\n')[0]?.trim() ?? '';
    if (language === 'mermaid' || ((!language || language === 'text') && MERMAID_START.test(firstLine))) {
      return source;
    }
  }
  return null;
}

function sanitizeDiagramSvg(svg: string): string {
  return svg
    .replace(/<script[\s\S]*?>[\s\S]*?<\/script>/gi, '')
    .replace(/\son\w+\s*=\s*(['"]).*?\1/gi, '');
}

function MermaidDiagram({ chart }: { chart: string }) {
  const reactId = useId().replace(/:/g, '');
  const [svg, setSvg] = useState<string | null>(null);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    let cancelled = false;
    setSvg(null);
    setError(null);

    loadMermaid()
      .then(async (mermaid) => {
        const { svg: rendered } = await mermaid.render(`mermaid-${reactId}`, chart);
        if (!cancelled) {
          setSvg(sanitizeDiagramSvg(rendered));
        }
      })
      .catch((renderError: unknown) => {
        if (!cancelled) {
          setError(renderError instanceof Error ? renderError.message : 'Unable to render diagram');
        }
      });

    return () => {
      cancelled = true;
    };
  }, [chart, reactId]);

  if (error) {
    return (
      <Box sx={{ mb: 2 }}>
        <Typography variant="caption" color="error" display="block" sx={{ mb: 1 }}>
          Diagram could not be rendered. Check the Mermaid syntax.
        </Typography>
        <Box component="pre" className="markdown-fence">
          {chart}
        </Box>
      </Box>
    );
  }

  if (!svg) {
    return (
      <Box
        sx={{
          mb: 2,
          p: 2,
          borderRadius: 2,
          bgcolor: CODE_FENCE_BG,
          color: 'text.secondary',
        }}
      >
        Rendering diagram…
      </Box>
    );
  }

  return (
    <Box
      className="mermaid-diagram"
      sx={{
        mb: 2,
        p: 2,
        borderRadius: 2,
        overflow: 'auto',
        bgcolor: CODE_FENCE_BG,
        border: '1px solid',
        borderColor: 'rgba(15, 23, 42, 0.12)',
        '& svg': { display: 'block', maxWidth: '100%', height: 'auto', mx: 'auto' },
      }}
      dangerouslySetInnerHTML={{ __html: svg }}
    />
  );
}

function MarkdownPre({ children }: { children?: ReactNode }) {
  const chart = mermaidSourceFromPre(children);
  if (chart) {
    return <MermaidDiagram chart={chart} />;
  }
  return <pre className="markdown-fence">{children}</pre>;
}

export function MarkdownViewer({ content, withSyntaxHighlight = false }: MarkdownViewerProps) {
  return (
    <Box
      className="markdown-viewer"
      sx={{
        color: 'text.primary',
        '& h1, & h2, & h3': { mt: 2, mb: 1, fontWeight: 600, color: 'text.primary' },
        '& p': { mb: 1.5, lineHeight: 1.7, color: 'text.primary' },
        '& ul, & ol': { pl: 3, mb: 1.5, color: 'text.primary' },
        '& li': { mb: 0.5 },
        '& strong': { color: 'text.primary', fontWeight: 700 },
        '& :not(pre) > code': {
          fontFamily: '"IBM Plex Mono", monospace',
          bgcolor: INLINE_CODE_BG,
          color: 'text.primary',
          px: 0.75,
          py: 0.15,
          borderRadius: 0.75,
          fontSize: '0.875em',
          border: '1px solid',
          borderColor: 'divider',
        },
        '& pre.markdown-fence': {
          bgcolor: CODE_FENCE_BG,
          color: CODE_FENCE_FG,
          p: 2.5,
          borderRadius: 2,
          overflow: 'auto',
          mb: 2,
          border: '1px solid',
          borderColor: 'rgba(15, 23, 42, 0.12)',
        },
        '& pre.markdown-fence code': {
          fontFamily: '"IBM Plex Mono", monospace',
          bgcolor: 'transparent',
          color: CODE_FENCE_FG,
          p: 0,
          fontSize: '0.875rem',
          lineHeight: 1.65,
          display: 'block',
          whiteSpace: 'pre',
        },
        '& pre.markdown-fence .hljs': {
          bgcolor: 'transparent',
          color: CODE_FENCE_FG,
          padding: 0,
        },
        '& a': { color: 'primary.main' },
        '& blockquote': {
          borderLeft: 3,
          borderColor: 'primary.main',
          pl: 2,
          color: 'text.secondary',
          my: 2,
          bgcolor: 'action.hover',
          py: 1,
          borderRadius: '0 8px 8px 0',
        },
        '& table': { width: '100%', borderCollapse: 'collapse', mb: 2 },
        '& th, & td': { border: 1, borderColor: 'divider', p: 1, textAlign: 'left' },
        '& th': { bgcolor: 'action.hover', fontWeight: 600 },
        '& img': { maxWidth: '100%', borderRadius: 1, my: 2 },
      }}
    >
      <ReactMarkdown
        remarkPlugins={[remarkGfm]}
        rehypePlugins={[
          [rehypeSanitize, sanitizeSchema],
          ...(withSyntaxHighlight ? [rehypeHighlight] : []),
        ]}
        components={markdownComponents}
      >
        {content || '_No content_'}
      </ReactMarkdown>
    </Box>
  );
}
